package com.logviewer.services;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class LvLogManagerTest {

    @Before
    public void before() {
        LvFileAccessManagerImpl.ROOT_PROVIDER = () -> Collections.singletonList(Paths.get("/"));
    }

    @Test
    public void testRootMatching() {
        LvFileAccessManagerImpl lm = create("/");
        assertEquals(Arrays.asList(Paths.get("/")), lm.getRoots());

        assert lm.isDirectoryVisible(Paths.get("D:/"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa"));
        assert lm.isDirectoryVisible(Paths.get("D:/zzz/aaa"));

        assert lm.isFileVisible(Paths.get("D:/aaa/b.log"));
        assert lm.isFileVisible(Paths.get("D:/aaa.log"));
    }

    @Test
    public void testFixedPatternMatching() {
        LvFileAccessManagerImpl lm = create("/aaa/b.log");
        assertEquals(Arrays.asList(Paths.get("/")), lm.getRoots());

        assert lm.isDirectoryVisible(Paths.get("D:/"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa"));
        assert lm.isDirectoryVisible(Paths.get("D:/zzz"));
        assert lm.isDirectoryVisible(Paths.get("D:/zzz/aaa"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/bbb"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/b.log"));

        assert lm.isFileVisible(Paths.get("D:/aaa/b.log"));
        assert !lm.isFileVisible(Paths.get("D:/aaa"));
        assert !lm.isFileVisible(Paths.get("D:/"));
        assert !lm.isFileVisible(Paths.get("D:/aaa/z.log"));
        assert !lm.isFileVisible(Paths.get("D:/ttt/b.log"));
        assert !lm.isFileVisible(Paths.get("D:/aaa/ttt/b.log"));
    }

    @Test
    public void testAsteriskPatternMatching() {
        LvFileAccessManagerImpl lm = create("/aaa/*.log");
        assertEquals(Arrays.asList(Paths.get("/")), lm.getRoots());

        assert lm.isDirectoryVisible(Paths.get("D:/"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa"));
        assert lm.isDirectoryVisible(Paths.get("D:/zzz"));
        assert lm.isDirectoryVisible(Paths.get("D:/zzz/aaa"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/bbb"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/b.log"));

        assert lm.isFileVisible(Paths.get("D:/aaa/b.log"));
        assert !lm.isFileVisible(Paths.get("D:/aaa"));
        assert !lm.isFileVisible(Paths.get("D:/"));
        assert lm.isFileVisible(Paths.get("D:/aaa/z.log"));
        assert lm.isFileVisible(Paths.get("D:/aaa/foo.log"));
        assert !lm.isFileVisible(Paths.get("D:/ttt/foo.log"));
        assert !lm.isFileVisible(Paths.get("D:/aaa/ttt/b.log"));
    }

    @Test
    public void testOpenDirectory() {
        LvFileAccessManagerImpl lm = create("/aaa/bbb/");
        assertEquals(Arrays.asList(Paths.get("/")), lm.getRoots());

        assert lm.isDirectoryVisible(Paths.get("D:/"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa"));
        assert lm.isDirectoryVisible(Paths.get("D:/zzz"));
        assert lm.isDirectoryVisible(Paths.get("D:/zzz/aaa"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/bbb"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/bbb/ccc"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/bbb/ccc/fff"));

        assert lm.isFileVisible(Paths.get("D:/aaa/bbb/b.log"));
        assert lm.isFileVisible(Paths.get("D:/aaa/bbb/ccc/v.log"));
        assert lm.isFileVisible(Paths.get("D:/aaa/bbb/ccc/ddddd/v.log"));
        assert !lm.isFileVisible(Paths.get("D:/aaa"));
        assert !lm.isFileVisible(Paths.get("D:/"));
        assert !lm.isFileVisible(Paths.get("D:/aaa/z.log"));
        assert !lm.isFileVisible(Paths.get("D:/aaa/bbb"));
        assert !lm.isFileVisible(Paths.get("D:/ttt/bbb/foo.log"));
        assert !lm.isFileVisible(Paths.get("D:/aaa/ttt/b.log"));
    }

    @Test
    public void testDoubleAsteriskEnd() {
        LvFileAccessManagerImpl lm = create("/aaa/bbb/**");
        assertEquals(Arrays.asList(Paths.get("/")), lm.getRoots());

        assert lm.isDirectoryVisible(Paths.get("D:/"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa"));
        assert lm.isDirectoryVisible(Paths.get("D:/zzz"));
        assert lm.isDirectoryVisible(Paths.get("D:/zzz/aaa"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/bbb"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/bbb/ccc"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/bbb/ccc/fff"));

        assert lm.isFileVisible(Paths.get("D:/aaa/bbb/b.log"));
        assert lm.isFileVisible(Paths.get("D:/aaa/bbb/ccc/v.log"));
        assert lm.isFileVisible(Paths.get("D:/aaa/bbb/ccc/ddddd/v.log"));
        assert !lm.isFileVisible(Paths.get("D:/aaa"));
        assert !lm.isFileVisible(Paths.get("D:/"));
        assert !lm.isFileVisible(Paths.get("D:/aaa/z.log"));
        assert !lm.isFileVisible(Paths.get("D:/aaa/bbb"));
        assert !lm.isFileVisible(Paths.get("D:/ttt/bbb/foo.log"));
        assert !lm.isFileVisible(Paths.get("D:/aaa/ttt/b.log"));
    }
    
    @Test
    public void testDoubleSlash() {
        LvFileAccessManagerImpl lm = create("/aaa/bbb//ccc/*.log");
        assertEquals(Arrays.asList(Paths.get("/")), lm.getRoots());
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/bbb/ccc"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa////bbb////ccc"));

        assert lm.isFileVisible(Paths.get("D:/aaa////bbb////ccc/f.log"));
        assert lm.isFileVisible(Paths.get("D:/aaa/bbb/ccc/f.log"));
    }

    @Test
    public void testAsteriskInDirectory() {
        LvFileAccessManagerImpl lm = create("/aaa/b*/ccc/*");
        assertEquals(Arrays.asList(Paths.get("/")), lm.getRoots());

        assert lm.isDirectoryVisible(Paths.get("D:/"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/bbb"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/b33"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/b33/ccc"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/b33/b33/ccc"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/fff"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/bbb/xxx"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/b33/ccc/ddd"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/b33/ccc/ddd/kkk"));
        assert lm.isDirectoryVisible(Paths.get("D:/zzz"));
        assert lm.isDirectoryVisible(Paths.get("D:/zzz/fff"));

        assert lm.isFileVisible(Paths.get("D:/aaa/bbb/ccc/b.log"));
        assert lm.isFileVisible(Paths.get("D:/aaa/b33/ccc/v.log"));
        assert !lm.isFileVisible(Paths.get("D:/aaa/b33/b33/ccc/v.log"));
        assert !lm.isFileVisible(Paths.get("D:/aaa/b33/v.log"));
        assert !lm.isFileVisible(Paths.get("D:/aaa/b33/ccc/ddd/v.log"));
        assert !lm.isFileVisible(Paths.get("D:/aaa/zzz/ccc/v.log"));
        assert lm.isFileVisible(Paths.get("D:/aaa/b/ccc/v.log"));
        assert !lm.isFileVisible(Paths.get("D:/aaaz/b/ccc/v.log"));
        assert !lm.isFileVisible(Paths.get("D:/v.log"));
    }

    @Test
    public void testOptionalDir() {
        LvFileAccessManagerImpl lm = create("/aaa/**/ccc/*");
        assertEquals(Arrays.asList(Paths.get("/")), lm.getRoots());

        assert lm.isDirectoryVisible(Paths.get("D:/"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/bbb"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/b33"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/b33/ccc"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/b33/b33/ccc"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/b33/g/e/y/ccc"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/b33/g/e/y/ccc/y"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/f/h/e/3/s/ccc"));
        assert lm.isDirectoryVisible(Paths.get("D:/zzz"));
        assert lm.isDirectoryVisible(Paths.get("D:/zzz/fff"));

        assert lm.isFileVisible(Paths.get("D:/aaa/bbb/ccc/b.log"));
        assert lm.isFileVisible(Paths.get("D:/aaa/ccc/v.log"));
        assert !lm.isFileVisible(Paths.get("D:/aaa/bbb/ccc/ff/b.log"));
        assert lm.isFileVisible(Paths.get("D:/aaa/b33/b33/ccc/v.log"));
        assert !lm.isFileVisible(Paths.get("D:/v.log"));
    }

    @Test
    public void testDoubleAsteriskInDirectory() {
        LvFileAccessManagerImpl lm = create("/aaa/b**/ccc/*");
        assertEquals(Arrays.asList(Paths.get("/")), lm.getRoots());

        assert lm.isDirectoryVisible(Paths.get("D:/"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/bbb"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/b33"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/b33/ccc"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/b33/b33/ccc"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/b33/g/e/y/ccc"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/b33/g/e/y/ccc/y"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/f/h/e/3/s/ccc"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/f"));
        assert lm.isDirectoryVisible(Paths.get("D:/zzz"));

        assert lm.isFileVisible(Paths.get("D:/aaa/bbb/ccc/b.log"));
        assert lm.isFileVisible(Paths.get("D:/aaa/b33/ccc/v.log"));
        assert lm.isFileVisible(Paths.get("D:/aaa/b33/b33/ccc/v.log"));
        assert lm.isFileVisible(Paths.get("D:/aaa/b33/f/h/b33/ccc/v.log"));

        assert !lm.isFileVisible(Paths.get("D:/aaa/f33/f/h/b33/ccc/v.log"));
        assert !lm.isFileVisible(Paths.get("D:/aaa/f33/f/h/b33/v.log"));
        assert !lm.isFileVisible(Paths.get("D:/aaa/bccc/v.log"));
        assert !lm.isFileVisible(Paths.get("D:/aaa/b33/v.log"));
        assert !lm.isFileVisible(Paths.get("D:/aaa/b33/ttt/v.log"));
        assert !lm.isFileVisible(Paths.get("D:/v.log"));
    }

    @Test
    public void testExtension() {
        LvFileAccessManagerImpl lm = create("**.log");
        assertEquals(Arrays.asList(Paths.get("/")), lm.getRoots());

        assert lm.isDirectoryVisible(Paths.get("D:/aaa"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/bbb"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/b33"));

        assert lm.isFileVisible(Paths.get("D:/aaa/bbb/ccc/b.log"));
        assert lm.isFileVisible(Paths.get("D:/aaa/v.log"));
        assert lm.isFileVisible(Paths.get("D:/v.log"));
        assert !lm.isFileVisible(Paths.get("D:/aaa/bbb/ccc/b.txt"));
        assert !lm.isFileVisible(Paths.get("D:/aaa/v.txt"));
        assert !lm.isFileVisible(Paths.get("D:/v.txt"));
    }

    @Test
    public void testExtension2() {
        LvFileAccessManagerImpl lm = create("*.log");
        assertEquals(Arrays.asList(Paths.get("/")), lm.getRoots());

        assert lm.isDirectoryVisible(Paths.get("D:/"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/bbb"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/b33"));

        assert lm.isFileVisible(Paths.get("D:/aaa/bbb/ccc/b.log"));
        assert lm.isFileVisible(Paths.get("D:/aaa/v.log"));
        assert lm.isFileVisible(Paths.get("D:/v.log"));
        assert !lm.isFileVisible(Paths.get("D:/aaa/bbb/ccc/b.txt"));
        assert !lm.isFileVisible(Paths.get("D:/aaa/v.txt"));
        assert !lm.isFileVisible(Paths.get("D:/v.txt"));
    }

    @Test
    public void testExtension3() {
        LvFileAccessManagerImpl lm = create("**/*.log");
        assertEquals(Arrays.asList(Paths.get("/")), lm.getRoots());

        assert lm.isDirectoryVisible(Paths.get("D:/"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/bbb"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/b33"));

        assert lm.isFileVisible(Paths.get("D:/aaa/bbb/ccc/b.log"));
        assert lm.isFileVisible(Paths.get("D:/aaa/v.log"));
        assert lm.isFileVisible(Paths.get("D:/v.log"));
        assert !lm.isFileVisible(Paths.get("D:/aaa/bbb/ccc/b.txt"));
        assert !lm.isFileVisible(Paths.get("D:/aaa/v.txt"));
        assert !lm.isFileVisible(Paths.get("D:/v.txt"));
    }

    @Test
    public void testExtension4() {
        LvFileAccessManagerImpl lm = create("*/*.log");
        assertEquals(Arrays.asList(Paths.get("/")), lm.getRoots());

        assert lm.isDirectoryVisible(Paths.get("D:/"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/bbb"));
        assert lm.isDirectoryVisible(Paths.get("D:/aaa/b33"));

        assert lm.isFileVisible(Paths.get("D:/aaa/bbb/ccc/b.log"));
        assert lm.isFileVisible(Paths.get("D:/aaa/v.log"));
    }

    @Test
    public void relativePath() {
        LvFileAccessManagerImpl lm = create("aaa/*/l*.log");
        assertEquals(Arrays.asList(Paths.get("/")), lm.getRoots());

        assert lm.isDirectoryVisible(Paths.get("D:/"));
        assert lm.isDirectoryVisible(Paths.get("D:/s"));
        assert lm.isDirectoryVisible(Paths.get("D:/s/fsd/ds/sdfs"));

        assert lm.isFileVisible(Paths.get("D:/aaa/bbb/l.log"));
        assert lm.isFileVisible(Paths.get("D:/aaa/bbb/lll.log"));
        assert !lm.isFileVisible(Paths.get("D:/aaaaa/bbb/lll.log"));
        assert !lm.isFileVisible(Paths.get("D:/aaa/lll.log"));
        assert lm.isFileVisible(Paths.get("D:/asd/aaaa/aaa/bbb/lll.log"));
        assert !lm.isFileVisible(Paths.get("D:/asd/aaaa/aaa/bbb/v.log"));
    }

    @Test
    public void relativePath2() {
        LvFileAccessManagerImpl lm = create("**/aaa/*/l*.log");
        assertEquals(Arrays.asList(Paths.get("/")), lm.getRoots());

        assert lm.isDirectoryVisible(Paths.get("D:/"));
        assert lm.isDirectoryVisible(Paths.get("D:/s"));
        assert lm.isDirectoryVisible(Paths.get("D:/s/fsd/ds/sdfs"));

        assert lm.isFileVisible(Paths.get("D:/aaa/bbb/l.log"));
        assert lm.isFileVisible(Paths.get("D:/aaa/bbb/lll.log"));
        assert !lm.isFileVisible(Paths.get("/aaaaa/bbb/lll.log"));
        assert !lm.isFileVisible(Paths.get("/aaa/lll.log"));
        assert lm.isFileVisible(Paths.get("D:/asd/aaaa/aaa/bbb/lll.log"));
        assert !lm.isFileVisible(Paths.get("/asd/aaaa/aaa/bbb/v.log"));
    }

    @Test
    public void testRelative6() {
        LvFileAccessManagerImpl lm = create("a*/ccc/*");
        assertEquals(Arrays.asList(Paths.get("/")), lm.getRoots());

        assert lm.isDirectoryVisible(Paths.get("D:/"));
        assert lm.isDirectoryVisible(Paths.get("D:/s"));
        assert lm.isDirectoryVisible(Paths.get("D:/s/fsd/ds/sdfs"));

        assert lm.isFileVisible(Paths.get("D:/aaa/ccc/l.log"));
        assert lm.isFileVisible(Paths.get("D:/foo/aaa/ccc/l.log"));
        assert lm.isFileVisible(Paths.get("D:/foo/bar/aaa/ccc/l.log"));
        assert !lm.isFileVisible(Paths.get("/ccc/l.log"));
        assert !lm.isFileVisible(Paths.get("/aaa/ccc/ddd/l.log"));
        assert !lm.isFileVisible(Paths.get("/bbb/ccc/l.log"));
        assert !lm.isFileVisible(Paths.get("/aaa/bbb/l.log"));
    }

    private LvFileAccessManagerImpl create(String ... pattern) {
        return new LvFileAccessManagerImpl(Stream.of(pattern).map(PathPattern::fromPattern).collect(Collectors.toList()));
    }

    @Test
    public void testRootSelection1() {
        LvFileAccessManagerImpl lm = create("a*/ccc/*", "/rrr/a.txt", "/rrr/b.txt");
        assertEquals(Arrays.asList(Paths.get("/")), lm.getRoots());
    }

    @Test
    public void testRootSelection11() {
        LvFileAccessManagerImpl lm = create("*/ccc/*", "/rrr/a.txt", "/rrr/b.txt");
        assertEquals(Arrays.asList(Paths.get("/")), lm.getRoots());
    }

    @Test
    public void testRootSelection12() {
        LvFileAccessManagerImpl lm = create("ccc/*", "/rrr/a.txt", "/rrr/b.txt");
        assertEquals(Arrays.asList(Paths.get("/")), lm.getRoots());
    }

    @Test
    public void testRootSelection2() {
        LvFileAccessManagerImpl lm = create("/rrr/a.txt", "/rrr/b.txt");
        assertEquals(Arrays.asList(Paths.get("/")), lm.getRoots());
    }

    @Test
    public void testRootSelection3() {
        LvFileAccessManagerImpl lm = create("/rrr/a.txt", "/rrr/foo/b.txt");
        assertEquals(Arrays.asList(Paths.get("/")), lm.getRoots());
    }

    @Test
    public void testRootSelection4() {
        LvFileAccessManagerImpl lm = create("/rrr/*", "/rrr/foo/b.txt");
        assertEquals(Arrays.asList(Paths.get("/")), lm.getRoots());
    }

    @Test
    public void testRootSelection5() {
        LvFileAccessManagerImpl lm = create("/rrr/bbb/*", "/rrr/bbb/b.txt");
        assertEquals(Arrays.asList(Paths.get("/")), lm.getRoots());
    }

}