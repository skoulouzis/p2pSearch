/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.p2psearch;

import java.util.List;
import net.tomp2p.peers.Number160;

/**
 *
 * @author S. Koulouzis
 */
class InvertedIndexEntry implements EntryI {

    /**
     *
     */
    private final String contentHash;
    /**
     *
     */
    private final String term;
    /**
     *
     */
    private final List<Number160> entryIDs;
    private final Integer tf;

    public InvertedIndexEntry(final String contentHash, final String term, final Integer tf, final List<Number160> entryIDs) {
        this.entryIDs = entryIDs;
        this.term = term;
        this.contentHash = contentHash;
        this.tf = tf;
    }

    /**
     * @return the term
     */
    public String getTerm() {
        return term;
    }

    /**
     * @return the entryIDs
     */
    public List<Number160> getEntryIDs() {
        return entryIDs;
    }

    @Override
    public String getContentHash() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
