package com.mapp.intelligence.tracking.consumer;

import com.mapp.intelligence.tracking.MappIntelligenceLogLevel;
import com.mapp.intelligence.tracking.MappIntelligenceUnitUtil;

import java.io.*;
import java.util.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class MappIntelligenceTrackingConsumerFileTest {
    private static List<String> contentMaxBatchSize = new ArrayList<>();
    private static List<String> maxPayloadSize = new ArrayList<>();
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final String tempFilePath = System.getProperty("user.dir") + "/src/test/resources/";
    private final String tempFilePathFail = System.getProperty("user.dir") + "src/test/resources/foo/";
    private final String tempFilePrefix = "mapp_intelligence_test";

    /**
     * @return long
     */
    private long getTimestamp() {
        return (new Date()).getTime();
    }

    @BeforeClass
    public static void createTestData() {
        String longText = "";
        longText += "Lorem%20ipsum%20dolor%20sit%20amet%2C%20consetetur%20sadipscing%20elitr%2C%20sed%20diam%20";
        longText += "nonumy%20eirmod%20tempor%20invidunt%20ut%20labore%20et%20dolore%20magna%20aliquyam%20erat%";
        longText += "2C%20sed%20diam%20voluptua.%20At%20vero%20eos%20et%20accusam%20et%20justo%20duo%20dolores%";
        longText += "20et%20ea%20rebum.%20Stet%20clita%20kasd%20gubergren%2C%20no%20sea%20takimata%20sanctus%20";
        longText += "est%20Lorem%20ipsum%20dolor%20sit%20amet.%20Lorem%20ipsum%20dolor%20sit%20amet%2C%20conset";
        longText += "etur%20sadipscing%20elitr%2C%20sed%20diam%20nonumy%20eirmod%20tempor%20invidunt%20ut%20lab";
        longText += "ore%20et%20dolore%20magna%20aliquyam%20erat%2C%20sed%20diam%20voluptua.%20At%20vero%20eos%";
        longText += "20et%20accusam%20et%20justo%20duo%20dolores%20et%20ea%20rebum.%20Stet%20clita%20kasd%20gub";
        longText += "ergren%2C%20no%20sea%20takimata%20sanctus%20est%20Lorem%20ipsum%20dolor%20sit%20amet.%20Lo";
        longText += "rem%20ipsum%20dolor%20sit%20amet%2C%20consetetur%20sadipscing%20elitr%2C%20sed%20diam%20no";
        longText += "numy%20eirmod%20tempor%20invidunt%20ut%20labore%20et%20dolore%20magna%20aliquyam%20erat%2C";
        longText += "%20sed%20diam%20voluptua.%20At%20vero%20eos%20et%20accusam%20et%20justo%20duo%20dolores%20";
        longText += "et%20ea%20rebum.%20Stet%20clita%20kasd%20gubergren%2C%20no%20sea%20takimata%20sanctus%20es";
        longText += "t%20Lorem%20ipsum%20dolor%20sit%20amet.%20Duis%20autem%20vel%20eum%20iriure%20dolor%20in%2";
        longText += "0hendrerit%20in%20vulputate%20velit%20esse%20molestie%20consequat%2C%20vel%20illum%20dolor";
        longText += "e%20eu%20feugiat%20nulla%20facilisis%20at%20vero%20eros%20et%20accumsan%20et%20iusto%20odi";
        longText += "o%20dignissim%20qui%20blandit%20praesent%20luptatum%20zzril%20delenit%20augue%20duis%20dol";
        longText += "ore%20te%20feugait%20nulla%20facilisi.%20Lorem%20ipsum%20dolor%20sit%20amet%2C%20consectet";
        longText += "uer%20adipiscing%20elit%2C%20sed%20diam%20nonummy%20nibh%20euismod%20tincidunt%20ut%20laor";
        longText += "eet%20dolore%20magna%20aliquam%20erat%20volutpat.%20Ut%20wisi%20enim%20ad%20minim%20veniam";
        longText += "%2C%20quis%20nostrud%20exerci%20tation%20ullamcorper%20suscipit%20lobortis%20nisl%20ut%20a";
        longText += "liquip%20ex%20ea%20commodo%20consequat.%20Duis%20autem%20vel%20eum%20iriure%20dolor%20in%2";
        longText += "0hendrerit%20in%20vulputate%20velit%20esse%20molestie%20consequat%2C%20vel%20illum%20dolor";
        longText += "e%20eu%20feugiat%20nulla%20facilisis%20at%20vero%20eros%20et%20accumsan%20et%20iusto%20odi";
        longText += "o%20dignissim%20qui%20blandit%20praesent%20luptatum%20zzril%20delenit%20augue%20duis%20dol";
        longText += "ore%20te%20feugait%20nulla%20facilisi.%20Nam%20liber%20tempor%20cum%20soluta%20nobis%20ele";
        longText += "ifend%20option%20congue%20nihil%20imperdiet%20doming%20id%20quod%20mazim%20placerat%20face";
        longText += "r%20possim%20assum.%20Lorem%20ipsum%20dolor%20sit%20amet%2C%20consectetuer%20adipiscing%20";
        longText += "elit%2C%20sed%20diam%20nonummy%20nibh%20euismod%20tincidunt%20ut%20laoreet%20dolore%20magn";
        longText += "a%20aliquam%20erat%20volutpat.%20Ut%20wisi%20enim%20ad%20minim%20veniam%2C%20quis%20nostru";
        longText += "d%20exerci%20tation%20ullamcorper%20suscipit%20lobortis%20nisl%20ut%20aliquip%20ex%20ea%20";
        longText += "commodo%20consequat.%20Duis%20autem%20vel%20eum%20iriure%20dolor%20in%20hendrerit%20in%20v";
        longText += "ulputate%20velit%20esse%20molestie%20consequat%2C%20vel%20illum%20dolore%20eu%20feugiat%20";
        longText += "nulla%20facilisis.%20At%20vero%20eos%20et%20accusam%20et%20justo%20duo%20dolores%20et%20ea";
        longText += "%20rebum.%20Stet%20clita%20kasd%20gubergren%2C%20no%20sea%20takimata%20sanctus%20est%20Lor";
        longText += "em%20ipsum%20dolor%20sit%20amet.%20Lorem%20ipsum%20dolor%20sit%20amet%2C%20consetetur%20sa";
        longText += "dipscing%20elitr%2C%20sed%20diam%20nonumy%20eirmod%20tempor%20invidunt%20ut%20labore%20et%";
        longText += "20dolore%20magna%20aliquyam%20erat%2C%20sed%20diam%20voluptua.%20At%20vero%20eos%20et%20ac";
        longText += "cusam%20et%20justo%20duo%20dolores%20et%20ea%20rebum.%20Stet%20clita%20kasd%20gubergren%2C";
        longText += "%20no%20sea%20takimata%20sanctus%20est%20Lorem%20ipsum%20dolor%20sit%20amet.%20Lorem%20ips";
        longText += "um%20dolor%20sit%20amet%2C%20consetetur%20sadipscing%20elitr%2C%20At%20accusam%20aliquyam%";
        longText += "20diam%20diam%20dolore%20dolores%20duo%20eirmod%20eos%20erat%2C%20et%20nonumy%20sed%20temp";
        longText += "or%20et%20et%20invidunt%20justo%20labore%20Stet%20clita%20ea%20et%20gubergren%2C%20kasd%20";
        longText += "magna%20no%20rebum.%20sanctus%20sea%20sed%20takimata%20ut%20vero%20voluptua.%20est%20Lorem";
        longText += "%20ipsum%20dolor%20sit%20amet.%20Lorem%20ipsum%20dolor%20sit%20ame.";

        for (int i = 0; i < 11 * 1000; i++) {
            contentMaxBatchSize.add("wt?p=300,0");
        }

        for (int i = 0; i < 9 * 1000; i++) {
            maxPayloadSize.add("wt?p=300,0&cp1=" + longText);
        }
    }

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(this.outContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(this.originalOut);

        MappIntelligenceUnitUtil.deleteFiles(this.tempFilePath, this.tempFilePrefix, ".tmp");
        MappIntelligenceUnitUtil.deleteFiles(this.tempFilePath, this.tempFilePrefix, ".log");
    }

    @Test
    public void testNewConsumerFile() {
        Map<String, Object> config = new HashMap<>();
        config.put("filePath", this.tempFilePathFail);
        config.put("filePrefix", this.tempFilePrefix);
        config.put("logger", MappIntelligenceUnitUtil.getCustomLogger());
        config.put("logLevel", MappIntelligenceLogLevel.DEBUG);
        new MappIntelligenceConsumerFile(config);

        assertTrue(this.outContent.toString().contains("java.io.IOException"));
    }

    @Test
    public void testFailedToSendBatch() {
        Map<String, Object> config = new HashMap<>();
        config.put("filePath", this.tempFilePathFail);
        config.put("filePrefix", this.tempFilePrefix);
        config.put("logger", MappIntelligenceUnitUtil.getCustomLogger());
        config.put("logLevel", MappIntelligenceLogLevel.DEBUG);
        MappIntelligenceConsumerFile consumer = new MappIntelligenceConsumerFile(config);

        List<String> data = new ArrayList<>();
        data.add("wt?p=300,0");
        assertFalse(consumer.sendBatch(data));
        assertTrue(this.outContent.toString().contains("java.io.IOException"));
    }

    @Test
    public void testMaxBatchSize() {
        Map<String, Object> config = new HashMap<>();
        config.put("filePath", this.tempFilePath);
        config.put("filePrefix", this.tempFilePrefix);
        config.put("logger", MappIntelligenceUnitUtil.getCustomLogger());
        config.put("logLevel", MappIntelligenceLogLevel.DEBUG);
        MappIntelligenceConsumerFile consumer = new MappIntelligenceConsumerFile(config);

        assertFalse(consumer.sendBatch(contentMaxBatchSize));
        assertTrue(this.outContent.toString().contains("Batch size is larger than 10000 req. (11000 req.)"));
    }

    @Test
    public void testMaxPayloadSize() {
        Map<String, Object> config = new HashMap<>();
        config.put("filePath", this.tempFilePath);
        config.put("filePrefix", this.tempFilePrefix);
        config.put("logger", MappIntelligenceUnitUtil.getCustomLogger());
        config.put("logLevel", MappIntelligenceLogLevel.DEBUG);
        MappIntelligenceConsumerFile consumer = new MappIntelligenceConsumerFile(config);

        assertFalse(consumer.sendBatch(maxPayloadSize));
        assertTrue(this.outContent.toString().contains("Payload size is larger than 24MB (34.7MB)"));
    }

    @Test
    public void testClosedFile() {
        MappIntelligenceUnitUtil.createFile(this.tempFilePath + this.tempFilePrefix + "-" + this.getTimestamp() + ".tmp");

        Map<String, Object> config = new HashMap<>();
        config.put("filePath", this.tempFilePath);
        config.put("filePrefix", this.tempFilePrefix);
        config.put("logger", MappIntelligenceUnitUtil.getCustomLogger());
        config.put("logLevel", MappIntelligenceLogLevel.DEBUG);
        MappIntelligenceConsumerFile consumer = new MappIntelligenceConsumerFile(config);

        try {
            FileWriter fileWriter = MappIntelligenceUnitUtil.getFileWriter(consumer);
            fileWriter.close();
        } catch (IOException e) {
            // do nothing
        }

        List<String> data = new ArrayList<>();
        data.add("wt?p=300,0");

        assertTrue(consumer.sendBatch(data));
        assertTrue(this.outContent.toString().contains("IOException"));
    }

    @Test
    public void testWriteBatchRequestInNewFile() {
        Map<String, Object> config = new HashMap<>();
        config.put("filePath", this.tempFilePath);
        config.put("filePrefix", this.tempFilePrefix);
        config.put("logger", MappIntelligenceUnitUtil.getCustomLogger());
        config.put("logLevel", MappIntelligenceLogLevel.DEBUG);
        MappIntelligenceConsumerFile consumer = new MappIntelligenceConsumerFile(config);

        List<String> data = new ArrayList<>();
        data.add("wt?p=300,0");
        assertTrue(consumer.sendBatch(data));

        assertTrue(this.outContent.toString().contains("Create new file mapp_intelligence_test"));
        assertTrue(this.outContent.toString().contains("Write batch data in mapp_intelligence_test"));
        assertEquals(
            "wt?p=300,0\n",
            MappIntelligenceUnitUtil.getFileContent(this.tempFilePath, this.tempFilePrefix, ".tmp")
        );
    }

    @Test
    public void testWriteBatchRequestInExistingNewFile1() {
        Map<String, Object> config = new HashMap<>();
        config.put("filePath", this.tempFilePath);
        config.put("filePrefix", this.tempFilePrefix);
        config.put("logger", MappIntelligenceUnitUtil.getCustomLogger());
        config.put("logLevel", MappIntelligenceLogLevel.DEBUG);
        MappIntelligenceConsumerFile consumer = new MappIntelligenceConsumerFile(config);

        List<String> data = new ArrayList<>();
        data.add("wt?p=300,0");
        data.add("wt?p=300,1");
        data.add("wt?p=300,2");
        assertTrue(consumer.sendBatch(data));

        data = new ArrayList<>();
        data.add("wt?p=300,3");
        data.add("wt?p=300,4");
        assertTrue(consumer.sendBatch(data));

        assertTrue(this.outContent.toString().contains("Write batch data in mapp_intelligence_test"));
        assertEquals(
            "wt?p=300,0\nwt?p=300,1\nwt?p=300,2\nwt?p=300,3\nwt?p=300,4\n",
            MappIntelligenceUnitUtil.getFileContent(this.tempFilePath, this.tempFilePrefix, ".tmp")
        );
    }

    @Test
    public void testWriteBatchRequestInExistingNewFile2() {
        Map<String, Object> config = new HashMap<>();
        config.put("filePath", this.tempFilePath);
        config.put("filePrefix", this.tempFilePrefix);
        config.put("logger", MappIntelligenceUnitUtil.getCustomLogger());
        config.put("logLevel", MappIntelligenceLogLevel.DEBUG);
        MappIntelligenceConsumerFile consumer = new MappIntelligenceConsumerFile(config);

        List<String> data = new ArrayList<>();
        data.add("wt?p=300,0");
        data.add("wt?p=300,1");
        data.add("wt?p=300,2");
        assertTrue(consumer.sendBatch(data));

        consumer = new MappIntelligenceConsumerFile(config);
        data = new ArrayList<>();
        data.add("wt?p=300,3");
        data.add("wt?p=300,4");
        assertTrue(consumer.sendBatch(data));

        assertTrue(this.outContent.toString().contains("Write batch data in mapp_intelligence_test"));
        assertEquals(
                "wt?p=300,0\nwt?p=300,1\nwt?p=300,2\nwt?p=300,3\nwt?p=300,4\n",
            MappIntelligenceUnitUtil.getFileContent(this.tempFilePath, this.tempFilePrefix, ".tmp")
        );
    }

    @Test
    public void testFileLimitReachedFileLines() {
        Map<String, Object> config = new HashMap<>();
        config.put("filePath", this.tempFilePath);
        config.put("filePrefix", this.tempFilePrefix);
        config.put("maxFileLines", 5);
        config.put("logger", MappIntelligenceUnitUtil.getCustomLogger());
        config.put("logLevel", MappIntelligenceLogLevel.DEBUG);
        MappIntelligenceConsumerFile consumer = new MappIntelligenceConsumerFile(config);

        for (int i = 0; i < 10; i++) {
            List<String> data = new ArrayList<>();
            data.add("wt?p=300," + i);

            consumer.sendBatch(data);
        }

        File[] tmp = MappIntelligenceUnitUtil.getFiles(this.tempFilePath, this.tempFilePrefix, ".tmp");
        File[] log = MappIntelligenceUnitUtil.getFiles(this.tempFilePath, this.tempFilePrefix, ".log");

        assertEquals(1, tmp.length);
        assertEquals(1, log.length);
        assertEquals(
            "wt?p=300,0\nwt?p=300,1\nwt?p=300,2\nwt?p=300,3\nwt?p=300,4\n",
            MappIntelligenceUnitUtil.getFileContent(this.tempFilePath, this.tempFilePrefix, ".log")
        );
        assertEquals(
            "wt?p=300,5\nwt?p=300,6\nwt?p=300,7\nwt?p=300,8\nwt?p=300,9\n",
            MappIntelligenceUnitUtil.getFileContent(this.tempFilePath, this.tempFilePrefix, ".tmp")
        );
    }

    @Test
    public void testFileLimitReachedFileDuration() {
        Map<String, Object> config = new HashMap<>();
        config.put("filePath", this.tempFilePath);
        config.put("filePrefix", this.tempFilePrefix);
        config.put("maxFileDuration", 1000);
        config.put("logger", MappIntelligenceUnitUtil.getCustomLogger());
        config.put("logLevel", MappIntelligenceLogLevel.DEBUG);
        MappIntelligenceConsumerFile consumer = new MappIntelligenceConsumerFile(config);

        List<String> data = new ArrayList<>();
        data.add("wt?p=300,0");
        data.add("wt?p=300,1");
        consumer.sendBatch(data);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        data = new ArrayList<>();
        data.add("wt?p=300,2");
        data.add("wt?p=300,3");
        consumer.sendBatch(data);

        File[] tmp = MappIntelligenceUnitUtil.getFiles(this.tempFilePath, this.tempFilePrefix, ".tmp");
        File[] log = MappIntelligenceUnitUtil.getFiles(this.tempFilePath, this.tempFilePrefix, ".log");

        assertEquals(1, tmp.length);
        assertEquals(1, log.length);
        assertEquals(
            "wt?p=300,0\nwt?p=300,1\n",
            MappIntelligenceUnitUtil.getFileContent(this.tempFilePath, this.tempFilePrefix, ".log")
        );
        assertEquals(
            "wt?p=300,2\nwt?p=300,3\n",
            MappIntelligenceUnitUtil.getFileContent(this.tempFilePath, this.tempFilePrefix, ".tmp")
        );
    }

    @Test
    public void testFileLimitReachedFileSize() {
        Map<String, Object> config = new HashMap<>();
        config.put("filePath", this.tempFilePath);
        config.put("filePrefix", this.tempFilePrefix);
        config.put("maxFileSize", 10);
        config.put("logger", MappIntelligenceUnitUtil.getCustomLogger());
        config.put("logLevel", MappIntelligenceLogLevel.DEBUG);
        MappIntelligenceConsumerFile consumer = new MappIntelligenceConsumerFile(config);

        List<String> data = new ArrayList<>();
        data.add("wt?p=300,0");
        data.add("wt?p=300,1");
        consumer.sendBatch(data);

        data = new ArrayList<>();
        data.add("wt?p=300,2");
        data.add("wt?p=300,3");
        consumer.sendBatch(data);

        File[] tmp = MappIntelligenceUnitUtil.getFiles(this.tempFilePath, this.tempFilePrefix, ".tmp");
        File[] log = MappIntelligenceUnitUtil.getFiles(this.tempFilePath, this.tempFilePrefix, ".log");

        assertEquals(1, tmp.length);
        assertEquals(1, log.length);
        assertEquals(
            "wt?p=300,0\nwt?p=300,1\n",
            MappIntelligenceUnitUtil.getFileContent(this.tempFilePath, this.tempFilePrefix, ".log")
        );
        assertEquals(
            "wt?p=300,2\nwt?p=300,3\n",
            MappIntelligenceUnitUtil.getFileContent(this.tempFilePath, this.tempFilePrefix, ".tmp")
        );
    }

    @Test
    public void testCreateFileWriterFailed() {
        String fileName = this.tempFilePath + this.tempFilePrefix + "-" + this.getTimestamp() + ".tmp";
        File file = MappIntelligenceUnitUtil.createFile(fileName);
        file.setReadOnly();

        Map<String, Object> config = new HashMap<>();
        config.put("filePath", this.tempFilePath);
        config.put("filePrefix", this.tempFilePrefix);
        config.put("maxFileLines", 5);
        config.put("logger", MappIntelligenceUnitUtil.getCustomLogger());
        config.put("logLevel", MappIntelligenceLogLevel.DEBUG);
        MappIntelligenceConsumerFile consumer = new MappIntelligenceConsumerFile(config);

        List<String> data = new ArrayList<>();
        data.add("wt?p=300,0");
        data.add("wt?p=300,1");
        consumer.sendBatch(data);

        File[] tmp = MappIntelligenceUnitUtil.getFiles(this.tempFilePath, this.tempFilePrefix, ".tmp");
        File[] log = MappIntelligenceUnitUtil.getFiles(this.tempFilePath, this.tempFilePrefix, ".log");

        assertEquals(1, tmp.length);
        assertEquals(0, log.length);
        assertEquals(
            "",
            MappIntelligenceUnitUtil.getFileContent(this.tempFilePath, this.tempFilePrefix, ".tmp")
        );
    }

    @Test
    public void testExtractTimestamp() {
        String fileName = this.tempFilePath + this.tempFilePrefix + "-5" + this.getTimestamp() + ".tmp";
        MappIntelligenceUnitUtil.createFile(fileName);

        Map<String, Object> config = new HashMap<>();
        config.put("filePath", this.tempFilePath);
        config.put("filePrefix", this.tempFilePrefix);
        config.put("maxFileLines", 5);
        config.put("logger", MappIntelligenceUnitUtil.getCustomLogger());
        config.put("logLevel", MappIntelligenceLogLevel.DEBUG);
        MappIntelligenceConsumerFile consumer = new MappIntelligenceConsumerFile(config);

        List<String> data = new ArrayList<>();
        data.add("wt?p=300,0");
        data.add("wt?p=300,1");
        consumer.sendBatch(data);

        File[] tmp = MappIntelligenceUnitUtil.getFiles(this.tempFilePath, this.tempFilePrefix, ".tmp");
        File[] log = MappIntelligenceUnitUtil.getFiles(this.tempFilePath, this.tempFilePrefix, ".log");

        assertEquals(1, tmp.length);
        assertEquals(1, log.length);
    }

    @Test
    public void testCurrentFileLines() {
        String fileName = this.tempFilePath + this.tempFilePrefix + "-" + this.getTimestamp() + ".tmp";
        File file = MappIntelligenceUnitUtil.createFile(fileName);
        file.setReadable(false, false);

        Map<String, Object> config = new HashMap<>();
        config.put("filePath", this.tempFilePath);
        config.put("filePrefix", this.tempFilePrefix);
        config.put("maxFileLines", 5);
        config.put("logger", MappIntelligenceUnitUtil.getCustomLogger());
        config.put("logLevel", MappIntelligenceLogLevel.DEBUG);
        MappIntelligenceConsumerFile consumer = new MappIntelligenceConsumerFile(config);

        List<String> data = new ArrayList<>();
        data.add("wt?p=300,0");
        data.add("wt?p=300,1");
        consumer.sendBatch(data);

        // java.io.FileNotFoundException (Permission denied)
        assertTrue(this.outContent.toString().contains("java.io.FileNotFoundException"));
    }

    @Test
    public void testFileLimitReached() {
        Map<String, Object> config = new HashMap<>();
        config.put("filePath", this.tempFilePath);
        config.put("filePrefix", this.tempFilePrefix);
        config.put("maxFileSize", 10);
        config.put("logger", MappIntelligenceUnitUtil.getCustomLogger());
        config.put("logLevel", MappIntelligenceLogLevel.DEBUG);
        MappIntelligenceConsumerFile consumer = new MappIntelligenceConsumerFile(config);

        for (int i = 0; i < 25; i++) {
            List<String> data = new ArrayList<>();
            data.add("wt?p=300," + i);
            consumer.sendBatch(data);

            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        File[] tmp = MappIntelligenceUnitUtil.getFiles(this.tempFilePath, this.tempFilePrefix, ".tmp");
        File[] log = MappIntelligenceUnitUtil.getFiles(this.tempFilePath, this.tempFilePrefix, ".log");

        assertEquals(1, tmp.length);
        assertEquals(24, log.length);
    }
}
