package com.sandy.capitalyst.server.core.ledger.loader;

import java.io.File ;
import java.io.FileInputStream ;
import java.sql.Date ;
import java.util.ArrayList ;
import java.util.Comparator ;
import java.util.List ;

import org.apache.log4j.Logger ;
import org.apache.poi.hssf.usermodel.HSSFWorkbook ;
import org.apache.poi.ss.usermodel.Cell ;
import org.apache.poi.ss.usermodel.Row ;
import org.apache.poi.ss.usermodel.Sheet ;
import org.apache.poi.ss.usermodel.Workbook ;

import com.sandy.capitalyst.server.core.CapitalystConstants.AccountType ;
import com.sandy.capitalyst.server.core.CapitalystConstants.Bank ;
import com.sandy.capitalyst.server.dao.account.Account ;
import com.sandy.capitalyst.server.dao.ledger.LedgerEntry ;
import com.sandy.common.util.StringUtil ;
import com.sandy.common.xlsutil.XLSRow ;
import com.sandy.common.xlsutil.XLSRowFilter ;
import com.sandy.common.xlsutil.XLSUtil ;
import com.sandy.common.xlsutil.XLSWrapper ;

public class ICICICreditCardLedgerImporter extends LedgerImporter {
    
    static final Logger log = Logger.getLogger( ICICICreditCardLedgerImporter.class ) ;
    
    private class RowFilter implements XLSRowFilter {
        public boolean accept( XLSRow row ) {
            String col0Val = row.getCellValue( 0 ) ;
            if( StringUtil.isEmptyOrNull( col0Val ) ) {
                return false ;
            }
            return true ;
        }
    }
    
    @Override
    public List<LedgerEntry> parseLedgerEntries( Account account, 
                                                 File file ) 
        throws Exception {
        
        if( !account.getAccountType().equals( AccountType.CREDIT.name() ) || 
            !account.getBankName().equals( Bank.ICICI.name() ) ) {
            throw new Exception( "Account is not ICICI Credit Card" ) ;
        }
        
        XLSWrapper wrapper = new XLSWrapper( file ) ;
        List<LedgerEntry> entries = new ArrayList<>() ;
        List<XLSRow> rows = wrapper.getRows( new RowFilter(), 14, 3, 9 ) ;
        
        float balance = extractBalance( file ) ;
        
        XLSUtil.printRows( rows ) ;
        for( XLSRow row : rows ) {
            entries.add( constructLedgerEntry( account, row, balance ) ) ;
        }
        
        entries.sort( new Comparator<LedgerEntry>() {
            public int compare( LedgerEntry le1, LedgerEntry le2 ) {
                return le1.getValueDate().compareTo( le2.getValueDate() ) ;
            }
        } ) ;
        
        return entries ;
    }
    
    public float extractBalance( File xlsFile ) throws Exception {
        
        Workbook workbook = null ;
        FileInputStream fIs = null ;
        
        try {
            fIs = new FileInputStream( xlsFile ) ;
            workbook = new HSSFWorkbook( fIs ) ; 
            Sheet sheet = workbook.getSheetAt( 0 ) ;
            Row row = sheet.getRow( 6 ) ;
            Cell cell = row.getCell( 7 ) ;
            
            String cellVal = cell.getStringCellValue() ;
            boolean isDebit = cellVal.endsWith( "Dr." ) ;
            cellVal = cellVal.substring( 4, cellVal.length()-4 ) ;
            cellVal = cellVal.replace( ",", "" ) ;
            Float val = Float.parseFloat( cellVal ) ;
            
            if( isDebit ) {
                val *= -1 ;
            }
            return val ;
        }
        finally {
            fIs.close() ;
            workbook.close() ;
        }
    }
    
    private LedgerEntry constructLedgerEntry( Account account, 
                                              XLSRow row, float balance ) 
        throws Exception {
        
        LedgerEntry entry = new LedgerEntry() ;
        entry.setAccount( account ) ;
        entry.setValueDate( new Date( SDF.parse( row.getCellValue( 0 ) )
                                         .getTime() ) ) ;
        entry.setRemarks( row.getCellValue( 1 ) ) ;
        
        String amtStr = row.getCellValue( 5 ).trim() ;
        boolean isDebit = amtStr.endsWith( "Dr." ) ;
        amtStr = amtStr.replace( ",", "" ) ;
        amtStr = amtStr.substring( 0, amtStr.length()-4 ) ;

        Float amt = Float.parseFloat( amtStr ) ;
        if( isDebit ) {
            entry.setAmount( -amt ) ;
        }
        else{
            entry.setAmount( amt ) ;
        }
        
        // Why are we generating has before setting the balance? Because,
        // there is no way to deterministically calculate balance from the 
        // input. 
        entry.generateHash() ;
        entry.setBalance( balance ) ;
        
        return entry ;
    }
}
