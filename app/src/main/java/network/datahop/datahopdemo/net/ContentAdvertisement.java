package network.datahop.datahopdemo.net;

import android.util.Log;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 *
 * A class representing the bloomfilter advertised by users containing the local videos*/

public class ContentAdvertisement {

    private BloomFilter bloomFilter;

    private static final String TAG = "ContentAdvertisement";
    // Empty constructor
    public ContentAdvertisement(){
        bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), 50,0.03);
    }

    public ContentAdvertisement(String filter){
        ByteArrayInputStream in = new ByteArrayInputStream(filter.getBytes());
        try {
           // Log.d(TAG,"Filter "+filter);
            bloomFilter = BloomFilter.readFrom(in, Funnels.stringFunnel(Charset.defaultCharset()));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bloomFilter.writeTo(out);
            //Log.d(TAG,"New filter "+out.size()+" "+out.toString());
        } catch (Exception e){Log.d(TAG,"Exception "+e);}

    }

    public ContentAdvertisement(byte[] filter){
        ByteArrayInputStream in = new ByteArrayInputStream(filter);
        try {
           // Log.d(TAG,"Filter "+filter);
            bloomFilter = BloomFilter.readFrom(in, Funnels.stringFunnel(Charset.defaultCharset()));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bloomFilter.writeTo(out);
            //Log.d(TAG,"New filter "+out.size()+" "+out.toString());
        } catch (Exception e){Log.d(TAG,"Exception "+e);}

    }

    public void addElement(String str)
    {
        Log.d(TAG,"Add element "+str);
        bloomFilter.put(str);

        // Log.d(TAG,"Contains elemment "+bloomFilter.mightContain("/source".getBytes()));
    }

    public boolean checkElement(String str)
    {
        return bloomFilter.mightContain(str);

    }

    public String getFilter()
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try{
            bloomFilter.writeTo(out);

            Log.d(TAG,"BF size "+out.size());
        } catch (IOException e)
        {
            Log.d(TAG,"Exception "+e);
        }

        return out.toString();
    }

    public byte[] getFilterBytes()
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try{
            bloomFilter.writeTo(out);

            Log.d(TAG,"BF size "+out.size());
        } catch (IOException e)
        {
            Log.d(TAG,"Exception "+e);
        }

        return out.toByteArray();
    }

    public BloomFilter getBloomFilter()
    {
        return bloomFilter;
    }


    public boolean compareFilters(String filter){


        return connectFilter(filter);
    }

    public boolean connectFilter(String filter){
        /*ContentAdvertisement ca = new ContentAdvertisement(filter);

         Log.d(TAG,"BF received size "+ca.getFilter());
        Log.d(TAG,"Hash "+ ca.getBloomFilter().hashCode() +" "+ca.checkElement("/source"));
        Log.d(TAG,"Hash "+bloomFilter.hashCode());

        if(filter.equals(getFilter()))return false;
       //if(source.equals("source")||id<)
        if(ca.checkElement("/source")) {
            try {
                ContentAdvertisement ca2 = (ContentAdvertisement) this.clone();
                ca2.addElement("/source");
                return !ca2.getFilter().equals(filter);
            } catch (Exception e) {
            }

           // return true;
        }else
            return ca.getBloomFilter().hashCode() < bloomFilter.hashCode();
*/      Log.d(TAG,"BF received size "+getFilter());
        return !getFilter().equals(filter);
        //return false;
    }

    public boolean connectFilter(byte[] filter){
        ContentAdvertisement ca = new ContentAdvertisement(filter);

        //Log.d(TAG,"BF received size "+ca.getFilter() +" "+getFilter());
        //Log.d(TAG,"Hash "+ ca.getBloomFilter().hashCode() +" "+ca.checkElement("/source"));
        //Log.d(TAG,"Hash "+getFilter()+" "+ca.getFilter()+" "+getFilter().equals(ca.getFilter()));

        return !getFilter().equals(ca.getFilter());
        //if(source.equals("source")||id<)
        /*if(ca.checkElement("/source")) {
            try {
                ContentAdvertisement ca2 = (ContentAdvertisement) this.clone();
                ca2.addElement("/source");
                return !ca2.getFilter().equals(filter);
            } catch (Exception e) {
            }

           // return true;
        }else
            return ca.getBloomFilter().hashCode() < bloomFilter.hashCode();*/
        //Log.d(TAG,"BF2 received size "+new String(filter) +" "+getFilter());
        //return !getFilter().equals(new String(filter));
        //return false;
    }

    //Putting elements into the filter
    //A BigInteger representing a key of some sort
    //Testing for element in set
}