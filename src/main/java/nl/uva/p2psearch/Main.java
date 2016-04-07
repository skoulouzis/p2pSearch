/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.p2psearch;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.hy.ArmenianAnalyzer;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

/**
 *
 * @author S. Koulouzis
 */
public class Main {

    static final Random RND = new Random(42L);
    private static final int PORT = 4001;
    private static Map<String, List<String>> schemaMap;
    private static final String METADATA_TABLE_NAME = "metadataTable";
    private static final String INVERTED_INDEX_TABLE_NAME = "invertedIndexTable";
    private static final String DATE_COLUMN_NAME = "date";
    private static final String CREATOR_COLUMN_NAME = "creator";
    private static final String DESCRIPTION_COLUMN_NAME = "description";
    private static final String FORMAT_COLUMN_NAME = "format";
    private static final String TAGS_COLUMN_NAME = "tags";
    private static final String[] SCHEMA_NAMES = new String[]{DATE_COLUMN_NAME, CREATOR_COLUMN_NAME, DESCRIPTION_COLUMN_NAME, FORMAT_COLUMN_NAME, TAGS_COLUMN_NAME};
    private static int maxNGrams = 3;

    /**
     *
     * @param args the arguments
     */
    public static void main(final String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        PeerDHT master = null;
        try {
            final int peerNr = 100;
            PeerDHT[] peers = null;
            try {
                peers = createAndAttachPeersDHT(peerNr, PORT);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            master = peers[0];
            bootstrap(peers);

            List<Entry> entries = getEntries();
            for (Entry e : entries) {
                PeerDHT p = peers[new Random().nextInt(peers.length)];
                insert(p, METADATA_TABLE_NAME, e);
                List<InvertedIndexEntry> ii = createInvertedIndexEntries(e);
                for (InvertedIndexEntry iie : ii) {
                    insert(p, INVERTED_INDEX_TABLE_NAME, iie);
                }   
            }

            String id = entries.get(0).getContentHash();
            for (String colName : SCHEMA_NAMES) {
                Object value = select(peers[new Random().nextInt(peers.length)], METADATA_TABLE_NAME, id, colName);
                System.out.println("single fetch for (" + id + "," + colName + "): [" + value + "]");
            }

        } catch (ParseException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (master != null) {
                master.shutdown();
            }
        }
    }

    public static PeerDHT[] createAndAttachPeersDHT(int nr, int port) throws IOException {
        PeerDHT[] peers = new PeerDHT[nr];
        for (int i = 0; i < nr; i++) {
            if (i == 0) {
                peers[0] = new PeerBuilderDHT(new PeerBuilder(new Number160(RND)).ports(port).start()).start();
            } else {
                peers[i] = new PeerBuilderDHT(new PeerBuilder(new Number160(RND)).masterPeer(peers[0].peer()).start()).start();
            }
        }
        return peers;
    }

    public static void bootstrap(PeerDHT[] peers) {
        //make perfect bootstrap, the regular can take a while
        for (PeerDHT peer1 : peers) {
            for (PeerDHT peer : peers) {
                peer1.peerBean().peerMap().peerFound(peer.peerAddress(), null, null, null);
            }
        }
    }

    private static List<Entry> getEntries() throws ParseException {
        int size = 10;
        List<Entry> e = new ArrayList<>(size);
        Entry entry = new Entry();
        entry.setDescription("The new version of the Aneurist database for use by the continuing consortium of the Aneurist project. It contains much of the original aneurist project cohort data but will now be augmented by an intiative to for an international aneurism registery. The dataset will continuously evolve and an associated collection of images will be available. During this implementation phase the dataset will not be available for sharing but once completed a formal application process will be available");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        entry.setCreationDate(sdf.parse("2014-05-14").getTime());
        entry.setCreator("name1");
        entry.setFormat("json");
        entry.setTags("Anuerism,Cerebrovascular,clip,stent,stroke,imaging,morphological,modelling");
        entry.setContentHash("0xd54fda3fabb494441fc31c29dbad5708b686e2f9");
        e.add(entry);

        entry = new Entry();
        entry.setDescription("This dataset is for use in VP2HF (http://vp2hf.eu), an EU funded framework 7 project (http://cordis.europa.eu/projects/rcn/110725_en.html) investigating Heart failure. Thanks to VPH-Share, partners can download some selected, anonymised fields for validation of computational tools among other interesting applications. In addition, through close contact with VPH-Share developers, the clinical data available from VP2HF clinical centres will be collected and curated with an adaptation of the VPH-Share database infrastructure. This is quite beneficial from the economical perspective of the VP2HF project, since alternatives would have involved spending substantial resources hiring a specialized company.");
        entry.setCreationDate(sdf.parse("2014-05-14").getTime());
        entry.setCreator("name1");
        entry.setFormat("csv");
        entry.setTags("Imaging,MRI,Echo,Cardiography,Heart faliure,Modelling");
        entry.setContentHash("0x960089c1028ed78931b2b025957d82ef7c8a0510");
        e.add(entry);

        entry = new Entry();
        entry.setDescription("The Minimal Interval Resonance Imaging in Alzheimer's Disease (MIRIAD) dataset is a series of longitudinal volumetric T1 MRI scans of 46 mild-moderate Alzheimer's subjects and 23 controls. It is made available for researchers to develop, validate and compare techniques, particularly for measurement of longitudinal volume change in serially acquired MR. With thanks to GlaxoSmithKline, the UK Alzheimer's society and the Medical Research Council. If you wish to gain access to this data set please register with the project team http://www.ucl.ac.uk/drc/research/miriad/ NOTE: This data was published as part of the VPH-Dare dementia research project but the owner of this resource is not permitted to share it with the wider comunity so please do not request access through the standard mechanisms. ");
        entry.setCreationDate(sdf.parse("2013-10-02").getTime());
        entry.setCreator("name2");
        entry.setFormat("dicom");
        entry.setTags("MIRIAD,Image Processing,Dementia");
        entry.setContentHash("0x45094710559af6e8f891ad8b3fec3b2023f2594b");
        e.add(entry);

        entry = new Entry();
        entry.setDescription("Metadata and link to cardiac patient meshes hosted at [AMDB](http://amdb.isd.kcl.ac.uk)");
        entry.setCreationDate(sdf.parse("2013-10-02").getTime());
        entry.setCreator("name3");
        entry.setFormat("xml");
        entry.setTags("left ventricle,cardiac,ventricular,AMDB,mesh,pre-term");
        entry.setContentHash("0x8924ac46143358915d2eaf572d382b6d4f3f9128");
        e.add(entry);

        return e;
    }

    /**
     *
     * @param peer the peer
     * @param tableName th
     * @param e s
     * @throws IOException s
     */
    private static void insert(final PeerDHT peer, final String tableName, final Entry e) throws IOException {

        Number160 tableKey = Number160.createHash(tableName);

        List<String> schema = getSchema(tableName);
        String id = e.getContentHash();
        for (String columnName : schema) {
            Number160 contentKey = combine(id, columnName);
            String data = getData(columnName, e);
            peer.put(tableKey).data(contentKey, new Data(data)).start().awaitUninterruptibly();
            contentKey = combine(columnName, id);
            peer.put(tableKey).data(contentKey, new Data(data)).start().awaitUninterruptibly();
        }
    }

    private static List<String> getSchema(String tableName) {
        if (schemaMap == null) {
            createSchemata();
        }

//         Number160 tableKey = Number160.createHash(tableName);
//         schemaMap.get(tableKey.toString());
        return schemaMap.get(tableName);
    }

    private static void createSchemata() {
        schemaMap = new HashMap<>();
        List<String> names = new ArrayList<>();

        names.add(DATE_COLUMN_NAME);
        names.add(CREATOR_COLUMN_NAME);
        names.add(DESCRIPTION_COLUMN_NAME);
        names.add(FORMAT_COLUMN_NAME);
        names.add(TAGS_COLUMN_NAME);
        schemaMap.put(METADATA_TABLE_NAME, names);

    }

    private static String getData(String columnName, Entry e) {
        switch (columnName) {
            case DATE_COLUMN_NAME:
                return String.valueOf(e.getCreationDate());
            case CREATOR_COLUMN_NAME:
                return e.getCreator();
            case DESCRIPTION_COLUMN_NAME:
                return e.getDescription();
            case FORMAT_COLUMN_NAME:
                return e.getFormat();
            case TAGS_COLUMN_NAME:
                return e.getTags();
            default:
                return null;
        }
    }

    /**
     * Combines two keys into one.
     *
     * @param key1 The first key
     * @param key2 The second key
     * @return the hash of the combined keys
     */
    private static Number160 combine(final String key1, final String key2) {
        Number160 firstKey = Number160.createHash(key1);
        Number160 secondKey = Number160.createHash(key2);
        return combine(firstKey, secondKey);
    }

    /**
     * Combines two keys into one.
     *
     * @param firstKey The first key
     * @param secondKey The second key
     * @return the hash of the combined keys
     */
    //CHECKSTYLE:OFF
    private static Number160 combine(final Number160 firstKey, final Number160 secondKey) {
        int[] val1 = firstKey.toIntArray();

        val1[0] = val1[0] ^ val1[4] ^ val1[3] ^ val1[2];
        val1[1] = val1[1] ^ val1[4] ^ val1[3] ^ val1[2];
        int[] val2 = secondKey.toIntArray();

        val2[2] = val2[2] ^ val2[0] ^ val2[1];
        val2[3] = val2[3] ^ val2[0] ^ val2[1];
        val2[4] = val2[4] ^ val2[0] ^ val2[1];

        val2[0] = val1[0];
        val2[1] = val1[1];

        return new Number160(val2);
    }

    private static Object select(PeerDHT peer, String tableName, String id, String columnName) throws ClassNotFoundException, IOException, InterruptedException {
        Number160 tableNameKey = Number160.createHash(tableName);

        Number160 contentKey = combine(id, columnName);

        FutureGet futureGet = peer.get(tableNameKey).contentKey(contentKey).start();
        futureGet.awaitUninterruptibly();

        return futureGet.data().object();
    }

    /**
     *
     * @param e s
     * @return s
     * @throws IOException s
     */
    private static List<InvertedIndexEntry> createInvertedIndexEntries(final Entry e) throws IOException {
        String text = e.getDescription();
        Map<Number160, Integer> dictionary = new HashMap<>();
        List<InvertedIndexEntry> list = new ArrayList<>();
        Analyzer analyzer = new ArmenianAnalyzer(Version.LUCENE_42, Utils.getCharArrayStopwords());
        try (TokenStream tokenStream = analyzer.tokenStream("field", new StringReader(text))) {
            PorterStemFilter psf = new PorterStemFilter(tokenStream);
            CharTermAttribute term = psf.addAttribute(CharTermAttribute.class);
            psf.reset();
            StringBuilder sb = new StringBuilder();
            while (psf.incrementToken()) {
                Integer tf;
                Number160 termKey = Number160.createHash(term.toString());
                if (dictionary.containsKey(termKey)) {
                    tf = dictionary.get(termKey);
                    tf++;
                } else {
                    tf = 1;
                }
                dictionary.put(termKey, tf);
                sb.append(term.toString()).append(" ");
                List<Number160> ll = new ArrayList<>();
                ll.add(Number160.createHash(e.getContentHash()));
                list.add(new InvertedIndexEntry(termKey.toString(), term.toString(), tf, ll));
            }
            StandardTokenizer source = new StandardTokenizer(Version.LUCENE_42, new StringReader(sb.toString()));
            TokenStream tokenStreamSF = new StandardFilter(Version.LUCENE_42, source);
            try (ShingleFilter sf = new ShingleFilter(tokenStreamSF, 2, maxNGrams)) {
                sf.setOutputUnigrams(false);
                CharTermAttribute charTermAttribute = sf.addAttribute(CharTermAttribute.class);
                sf.reset();
                while (sf.incrementToken()) {
                    String word = charTermAttribute.toString();
                    String ng = word.replaceAll(" ", "_");
                    Integer tf;
                    Number160 termKey = Number160.createHash(ng);
                    if (dictionary.containsKey(termKey)) {
                        tf = dictionary.get(termKey);
                        tf++;
                    } else {
                        tf = 1;
                    }
                    dictionary.put(termKey, tf);
                    List<Number160> ll = new ArrayList<>();
                    ll.add(Number160.createHash(e.getContentHash()));
                    list.add(new InvertedIndexEntry(termKey.toString(), term.toString(), tf, ll));
                }
            }
        }
        return list;
    }

    private static void insert(PeerDHT p, String INVERTED_INDEX_TABLE_NAME, InvertedIndexEntry iie) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
