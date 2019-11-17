package com.sandy.capitalyst.server.dao.ledger;

import java.sql.Date ;
import java.text.SimpleDateFormat ;

import javax.persistence.Entity ;
import javax.persistence.GeneratedValue ;
import javax.persistence.GenerationType ;
import javax.persistence.Id ;
import javax.persistence.JoinColumn ;
import javax.persistence.ManyToOne ;
import javax.persistence.Table ;

import com.sandy.capitalyst.server.dao.account.Account ;
import com.sandy.common.util.StringUtil ;

@Entity
@Table( name = "account_ledger" )
public class LedgerEntry {
    
    private static final SimpleDateFormat HASH_SDF = new SimpleDateFormat( "dd/MM/yyyy" ) ;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id ;
    
    @ManyToOne
    @JoinColumn( name="account_id" )
    private Account account ;
    
    private Date valueDate = null ;
    private String remarks = null ;
    private float amount = 0 ;
    private String chequeNumber = null ;
    private float balance = 0 ;
    private String hash = null ;
    
    public Integer getId() {
        return id ;
    }
    
    public void setId( Integer id ) {
        this.id = id ;
    }

    public Account getAccount() {
        return account ;
    }

    public void setAccount( Account account ) {
        this.account = account ;
    }

    public Date getValueDate() {
        return valueDate ;
    }

    public void setValueDate( Date valueDate ) {
        this.valueDate = valueDate ;
    }

    public String getRemarks() {
        return remarks ;
    }

    public void setRemarks( String remarks ) {
        this.remarks = remarks ;
    }

    public float getAmount() {
        return amount ;
    }

    public void setAmount( float amount ) {
        this.amount = amount ;
    }

    public float getBalance() {
        return balance ;
    }

    public void setBalance( float balance ) {
        this.balance = balance ;
    }
    
    public String getChequeNumber() {
        return chequeNumber ;
    }

    public void setChequeNumber( String chequeNumber ) {
        this.chequeNumber = chequeNumber ;
    }
    
    public String getHash() {
        return hash ;
    }

    public void setHash( String hash ) {
        this.hash = hash ;
    }

    public boolean isCredit() {
        return amount > 0 ;
    }
    
    public boolean isChequePayment() {
        return StringUtil.isNotEmptyOrNull( chequeNumber ) ;
    }
    
    public String generateHash() throws Exception {
        
        StringBuffer buffer = new StringBuffer() ;
        buffer.append( account.getAccountNumber() )
              .append( HASH_SDF.format( valueDate ) )
              .append( remarks )
              .append( Float.toString( amount ) )
              .append( Float.toString( getBalance() ) ) ;
        this.hash = StringUtil.getHash( buffer.toString() ) ;
        return this.hash ;
    }
    
    public String toString() {
        
        StringBuffer buffer = new StringBuffer() ;
        buffer.append( "LedgerEntry [" ).append( "\n" )
              .append( "  Account = " + account.getShortName() ).append( "\n" )
              .append( "  Value date = " + HASH_SDF.format( valueDate ) ).append( "\n" )
              .append( "  Remarks = " + remarks ).append( "\n" )
              .append( "  Amount = " + amount ).append( "\n" )
              .append( "]" ) ;
        return buffer.toString() ;
    }
}