package com.logviewer.services;

import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.*;

public class PathPatternTest {

    @Test
    public void fromDirectory() {
        PathPattern d = PathPattern.directory(Paths.get("D:/aaa/bbb"));

        assertEquals(Paths.get("D:/aaa/bbb"), d.getPrefix());

        assertTrue(d.matchFile(Paths.get("D:/aaa/bbb/l.log")));
        assertTrue(d.matchFile(Paths.get("D:/aaa/bbb/ccc/ddd/l.log")));
        assertFalse(d.matchFile(Paths.get("D:/aaa/z.log")));
        assertFalse(d.matchFile(Paths.get("D:/z.log")));
        assertFalse(d.matchFile(Paths.get("D:/aaa/ggg/z.log")));

        assertTrue(d.matchDir(Paths.get("D:/aaa/bbb")));
        assertTrue(d.matchDir(Paths.get("D:/aaa/bbb/ccc/ddd")));
        assertTrue(d.matchDir(Paths.get("D:/aaa")));
        assertTrue(d.matchDir(Paths.get("D:/")));

        assertFalse(d.matchDir(Paths.get("D:/aaa/zzz")));
        assertFalse(d.matchDir(Paths.get("D:/aaa2/bbb/")));

    }

    @Test
    public void fromFile() {
        PathPattern d = PathPattern.file(Paths.get("D:/aaa/bbb/l.log"));
        assertEquals(Paths.get("D:/aaa/bbb"), d.getPrefix());
        assertTrue(d.matchFile(Paths.get("D:/aaa/bbb/l.log")));
        assertFalse(d.matchFile(Paths.get("D:/aaa/l.log")));
        assertFalse(d.matchFile(Paths.get("D:/aaa/bbb/z.log")));

        assertFalse(d.matchDir(Paths.get("D:/aaa/bbb/l.log")));
        assertFalse(d.matchDir(Paths.get("D:/aaa/zzz")));
        assertTrue(d.matchDir(Paths.get("D:/aaa/bbb")));
        assertTrue(d.matchDir(Paths.get("D:/aaa")));
        assertTrue(d.matchDir(Paths.get("D:/")));
    }
}