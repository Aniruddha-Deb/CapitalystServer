package com.sandy.capitalyst.server.core.network;

import java.io.BufferedReader ;
import java.io.File ;
import java.io.InputStream ;
import java.io.InputStreamReader ;
import java.util.HashMap ;
import java.util.Map ;

import org.apache.log4j.Logger ;

import com.sandy.capitalyst.server.CapitalystServer ;
import com.sandy.capitalyst.server.config.CapitalystConfig ;
import com.sandy.capitalyst.server.util.StringUtil ;

import okhttp3.Cache ;
import okhttp3.OkHttpClient ;
import okhttp3.Request ;
import okhttp3.Response ;

public class HTTPResourceDownloader {
    
    private static final Logger log = Logger.getLogger( HTTPResourceDownloader.class ) ;

    private static OkHttpClient client = null ;
    private static HTTPResourceDownloader instance = null ;
    
    public static HTTPResourceDownloader instance() {
        if( instance == null ) {
            instance = new HTTPResourceDownloader() ; 
        }
        return instance ;
    }
    
    private HTTPResourceDownloader() {
        initializeHttpClient() ;
    }
    
    private void initializeHttpClient() {
        
        CapitalystConfig config = CapitalystServer.getConfig() ;
        okhttp3.OkHttpClient.Builder builder = new OkHttpClient.Builder() ;
        
        if( config != null ) {
            File workspaceDir = config.getWorkspaceDir() ;
            File httpCacheDir = new File( workspaceDir, "http_cache" ) ;
            
            if( !httpCacheDir.exists() ) {
                httpCacheDir.mkdirs() ;
            }
            builder.cache( new Cache( httpCacheDir, 5*1025*1024 ) ) ;
        }
        
        client = builder.build() ;
    }
    
    public String getResource( String url ) 
        throws Exception {
        return getResource( url, (Map<String, String>)null ) ;
    }
    
    public String getResource( String url, String headerResourceName ) 
        throws Exception {
        
        Map<String, String> headers = loadHeaders( headerResourceName ) ;
        return getResource( url, headers ) ;
    }
    
    public String getResource( String url, Map<String, String> headers ) 
        throws Exception {
        
        byte[] contents = getResourceAsBytes( url, headers ) ;
        return new String( contents ) ;
    }
    
    private byte[] getResourceAsBytes( String url , Map<String, String> headers )
        throws Exception {
        
        okhttp3.Request.Builder builder = null ;
        builder = new Request.Builder().url( url ) ;
        
        log.debug( "Downloading resource from " + url ) ;
        if( headers != null && !headers.isEmpty() ) {
            for( String key : headers.keySet() ) {
                String value = headers.get( key ) ;
                builder.addHeader( key, value ) ;
                log.debug( "\tHeader : " + key + " :: " + value ) ;
            }
        }
        
        long startTime = System.currentTimeMillis() ;
        Request request = builder.build() ;
        Response response = client.newCall( request ).execute() ;
        long endTime = System.currentTimeMillis() ;
        
        int responseCode = response.code() ;
        log.debug( "Response code = " + responseCode ) ;
        
        int timeTaken = (int)( endTime - startTime ) ;
        log.debug( "Time taken = " + timeTaken + " millis" ) ;
        
        if( responseCode == 404 ) {
            throw new HTTPException404() ;
        }
        
        long contentLength = response.body().contentLength() ;
        log.debug( "Content length = " + (int)(contentLength/1024) + " KB" ) ;
        
        byte[] responseBody = response.body().bytes() ;
        response.body().close() ;
        
        return responseBody ;
    }
    
    private Map<String, String> loadHeaders( String headerResourceName ) 
        throws Exception {
        
        InputStream is = null ;
        BufferedReader reader = null ;
        Map<String, String> headers = new HashMap<>() ;
        String resPath = "/http_headers/" + headerResourceName ;
        
        is = getClass().getResourceAsStream( resPath ) ;
        if( is == null ) {
            throw new RuntimeException( "Header file not found @ " + resPath ) ;
        }
        
        reader = new BufferedReader( new InputStreamReader( is ) ) ;
        String line = reader.readLine() ;
        while( line != null ) {
            if( StringUtil.isNotEmptyOrNull( line ) ) {
                if( !line.trim().startsWith( "#" ) ) {
                    String[] parts = line.trim().split( ":" ) ;
                    headers.put( parts[0].trim(), parts[1].trim() ) ;
                }
            }
            line = reader.readLine() ;
        }
        
        return headers ;
    }
}
