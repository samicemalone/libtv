package uk.co.samicemalone.libtv.matcher.path;

import org.junit.Before;
import org.junit.Test;
import uk.co.samicemalone.libtv.FileSystemEnvironment;
import uk.co.samicemalone.libtv.MockFileSystem;
import uk.co.samicemalone.libtv.model.AliasMap;

import java.nio.file.Path;

import static org.junit.Assert.*;

public class AliasedTVLibraryTest extends FileSystemEnvironment {

    AliasedTVLibrary library;
    String show = "It's Always Sunny In Philadelphia";
    String alias = "Its Always Sunny In Philadelphia";


    @Before
    public void setUp() throws Exception {
        AliasMap map = new AliasMap();
        map.addAlias(alias, show);
        library = new AliasedTVLibrary(MockFileSystem.getSourceFolders(), map);
    }

    @Test
    public void testGetSeasonsPath() throws Exception {
        Path expResult = MockFileSystem.getShowDir(show).toPath();
        Path result = library.getSeasonsPath(alias);
        assertEquals(expResult, result);
    }

    @Test
    public void testNewEpisodesPath() throws Exception {
        Path expResult = MockFileSystem.getShowDir(show).toPath().resolve("Season 1");
        Path result = library.newEpisodesPath(alias, 1, StandardTVPath.SeasonFormat.SEASON_PADDED);
        assertEquals(expResult, result);
    }
}