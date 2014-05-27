libtv
=====

libtv is a Java library for matching TV episodes based on the file path.

Episode Filename Formats
========================

Before attempting to match an episode filename, common strings are removed that may cause false matches e.g. 1080p, ac3, h264 etc...

Episode files may contain more than one episode number. See the [Wiki](https://github.com/samicemalone/libtv/wiki/Episode-Naming-Formats) for example multi-episode filenames supported.

Word separators (_- .) are interchangable to allow more flexibility in matching. Example matchable file names:

  - show.name.s01e02.mkv
  - Show Name - 1x02.mkv
  - show_name.101.mkv
  - show-name-pt-iv.mkv

See the [Wiki](https://github.com/samicemalone/libtv/wiki/Episode-Naming-Formats) for more detailed naming formats

TV Paths
========

The `TVPath` interface models a TV directory structure, allowing access to filesystem paths containing TV show seasons and episodes.

`StandardTVPath` models a standard TV directory structure. For example:

  - /path/to/TV/Show Name/Season 1/EpisodeFile.mkv
  - /path/to/TV/Show Name/Season 01/EpisodeFile.mkv
  - /path/to/TV/Show Name/Series 1/EpisodeFile.mkv
  - /path/to/TV/Show Name/Series 01/EpisodeFile.mkv

TV Libraries
============

`StandardTVLibrary` is a `TVPath` implementation that uses a collection of `StandardTVPath` objects to provide access to filesystem paths containing TV show seasons and episodes.

`AliasedTVLibrary` extends `StandardTVLibrary` to provide support for TV show aliases. This can be useful for foreign remakes of shows or shows with the duplicate names that are released in different years. For example, **"House of Cards"** could refer to **"House of Cards (UK)"**, **"House of Cards (US)"**, **"House of Cards (1990)"** or **House of Cards (2013)**.

Matching
========

EpisodeMatch
-------------

An `EpisodeMatch` object is used to represent a TV episode. It stores the TV show name, season number, episode numbers and the file path.

EpisodeMatcher
--------------

EpisodeMatcher matches a path, or a collection of paths, to the corresponding `EpisodeMatch`. When using a collection of paths, the episodes are expected to be for the same show and season.

An episode can be considered a match if only an episode number is found. The show and season number will be present if found. For example:
```java
// Match 24, season 1, episode 2
Path p = Paths.get("24 - 1x02 - Name.mkv");
EpisodeMatch m = new EpisodeMatcher().match(p);
// Match The Pacific, episode 4 (season EpisodeMatch.NO_SEASON)
p = Paths.get("The Pacific - Pt IV.mkv");
m = new EpisodeMatcher().match(p);
// Match 24, season 1, episode 5
p = Paths.get("/path/to/TV/24/Season 1/");
List<Path> l = PathUtil.listPaths(p, new VideoFilter());
m = new EpisodeMatcher().match(l, 5);
```
`EpisodeMatcher` can match individual episodes, a range of episodes, the remaining episodes in a season, the largest episode number in a season, and whole seasons. Episodes can also be matched according to the criteria defined by a `MatchCondition` implementation.

TVMatcher
---------

TVMatcher matches an episode file path to an EpisodeMatch with the ability to specify which TV show elements are required.

By default, episode matching is deferred to the implementations of `TVMatcher.Matcher` in package `uk.co.samicemalone.libtv.matcher.tv`. Matching can be deferred to a `TVMatcherOptions` instance. See the [Wiki](https://github.com/samicemalone/libtv/wiki/Episode-Naming-Formats) for more information on the naming formats

Example (using default `TVMatcherOptions`):

```java
// Throws MatchException
Path p = Paths.get("Not.A.TV.Episode.avi");
EpisodeMatch m = new TVMatcher().matchOrThrow(p);
// Match The Pacific, no season, episode 8
p = Paths.get("The Pacific - Part VIII.mkv");
m = new TVMatcher().match(p);
// m = null
m = new TVMatcher().matchElement(p, TVMatcher.MatchElement.SEASON); 
// Throws MatchElementNotFoundException
m = new TVMatcher().matchElementOrThrow(p, TVMatcher.MatchElement.SEASON);
// Match The Pacific, no season, episode 8
m = new TVMatcher().matchElementOrThrow(p, TVMatcher.MatchElement.SHOW); 
// Throws MatchElementNotFoundException
m = new TVMatcher().matchElementOrThrow(p, TVMatcher.MatchElement.ALL);
```

Example (using `TVMatcherOptions`):

```java
Path p = Paths.get("The Pacific", "Season 1", "The Pacific - Part VIII.mkv");
// Match The Pacific, no season, episode 8
TVMatcherOptions o = new TVMatcherOptions();
EpisodeMatch m = new TVMatcher(o).match(p);
// Match The Pacific, season 1, episode 8
TVMatcherOptions o = new TVMatcherOptions(new StandardTVElementMatcher());
EpisodeMatch m = new TVMatcher(o).match(p);
// Match The Pacific, season 1, episode 8
TVMatcherOptions o = new TVMatcherOptions().fallback(new StandardTVElementMatcher());
EpisodeMatch m = new TVMatcher(o).match(p);
```

TVEpisodeMatcher
----------------

`TVEpisodeMatcher` can use a `StandardTVPath` to match TV episodes or sets of TV episodes in a variety of ways:

* Individual episodes
* All episodes in a season
* Ranges of episodes (including ranges across seasons)
* Ranges of seasons
* Remaining episodes in a season
* Remaining seasons
* Latest episode for a TV show
* Largest episode in a season
* Largest season number
* All episodes

EpisodeNavigator
----------------
`EpisodeNavigator` can be used to navigate to an episode via an offset. In other words, find the previous, current or next episode. For example:

```java
// assume TV path contains "24", seasons 1-8, 24 episodes per season
Path tvRoot = Paths.get("/path/to/root/TV");
StandardTVPath tvPath = new StandardTVPath(tvRoot);
EpisodeNavigator nav = new EpisodeNavigator(new TVEpisodeMatcher(), tvPath);
EpisodeMatch m = new EpisodeMatch("24", 1, 24); // 24 - s01e24
// match next episode = 24 - s02e01
m = nav.navigate(m, EpisodeNavigator.Pointer.NEXT);
// match current episode = 24 -s02e01
m = nav.navigate(m, EpisodeNavigator.Pointer.CUR);
// match previous episode = 24 - s01e24
m = nav.navigate(m, EpisodeNavigator.Pointer.PREV);
```

TV Map
===============
`TVMap` creates a mapping between TV shows, seasons and episodes to allow for more efficient episode lookups.

Episode paths that contain more than one episode number are duplicated in map to allow for quick retrieval using partial episode number matches e.g. searching for episode 1 when the map contains a double episode [1, 2].

TVMap uses a `Collection` -like interface but does not implement `Collection`. Some example methods:

```java
// Assume a collection of shows "24", "Scrubs", "Friends"
// with 2 seasons each, consisting of 12 episodes
Collection<EpisodeMatch> matches = ...;
TVMap map = new TVMap(matches);
map.containsShow("24"); // true
map.containsSeason("24", 1); // true
map.contains("24", 1, 12); // true
map.getEpisodes("24"); // Set of episodes for 24 (s01, s02 = 24)
map.getSeasonEpisodes("24", 1); // Set of 12 episodes for 24 s01
map.getSeasonCount("24"); // 2
map.getShowCount(); // 3
map.removeSeason("Scrubs", 1);
map.removeShow("Friends");
```

TV Show Case
=========
TV show case is variant of title case but makes exceptions to capitalise bracketed strings which are common for foreign TV show remakes. For example:

```java
ShowCase.convert("the.office.(us)"); // The Office (US)
ShowCase.convert("brooklyn nine-nine"); // Brooklyn Nine-Nine
ShowCase.convert("two_and_a_half_men"); // Two and a Half Men
ShowCase.convert("the IT crowd"); // The IT Crowd
ShowCase.convert("house of cards (2013)"); // House of Cards (2013)

```

Comparators
===========

`EpisodeComparator` compares EpisodeMatch objects in ascending order of show name, season number, starting episode number in the range of episode numbers, ending episode number in the range of episode numbers.

E.g. 24 s01e01 **&lt;** 24 s01e01e02 **&lt;** 24 s01e01e02e03 **&lt;** 24 s01e02e03

`EpisodeNoComparator` compares EpisodeMatch instances in the same season by episode number ascending using the criteria: starting episode number in the range of episode numbers, the ending episode number in the range of episode numbers.

E.g. s01e01 **&lt;** s01e01e02 **&lt;** s01e01e02e03 **&lt;** s01e02e03

Copyright
=========
Copyright (c) 2014, Sam Malone. All rights reserved.

Licensing
=========
The libtv source code, binaries and documentation are licensed under a BSD License.
See LICENSE for details.

Author
======
Sam Malone
    