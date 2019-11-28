package com.sandy.capitalyst.server.api.ledger;

import java.util.Date ;

public class LedgerSearchCriteria {

    private Integer accountId = null ;
    private Date startDate = null ;
    private Date endDate = null ;
    private Float lowerAmtThreshold = null ;
    private Float upperAmtThreshold = null ;
    private String customRule = null ;
    private boolean showOnlyUnclassified = false ;
    
    public LedgerSearchCriteria() {}

    public void setAccountId( Integer val ) {
        this.accountId = val ;
    }
        
    public Integer getAccountId() {
        return this.accountId ;
    }

    public void setStartDate( Date val ) {
        this.startDate = val ;
    }
        
    public Date getStartDate() {
        return this.startDate ;
    }

    public void setEndDate( Date val ) {
        this.endDate = val ;
    }
        
    public Date getEndDate() {
        return this.endDate ;
    }

    public Float getLowerAmtThreshold() {
        return lowerAmtThreshold ;
    }

    public void setLowerAmtThreshold( Float amt ) {
        this.lowerAmtThreshold = amt ;
    }

    public Float getUpperAmtThreshold() {
        return upperAmtThreshold ;
    }

    public void setUpperAmtThreshold( Float amt ) {
        this.upperAmtThreshold = amt ;
    }
    
    public String getCustomRule() {
        return customRule ;
    }

    public void setCustomRule( String customRule ) {
        this.customRule = customRule ;
    }

    public boolean isShowOnlyUnclassified() {
        return showOnlyUnclassified ;
    }

    public void setShowOnlyUnclassified( boolean bool ) {
        this.showOnlyUnclassified = bool ;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder( "LedgerSearchCriteria [\n" ) ; 

        builder.append( "   accountId = " + this.accountId + "\n" ) ;
        builder.append( "   startDate = " + this.startDate + "\n" ) ;
        builder.append( "   endDate = " + this.endDate + "\n" ) ;
        builder.append( "   lowerAmtThreshold = " + this.lowerAmtThreshold + "\n" ) ;
        builder.append( "   upperAmtThreshold = " + this.upperAmtThreshold + "\n" ) ;
        builder.append( "   customRule = " + this.customRule + "\n" ) ;
        builder.append( "   showOnlyUnclassified = " + this.showOnlyUnclassified + "\n" ) ;
        builder.append( "]" ) ;
        
        return builder.toString() ;
    }
}