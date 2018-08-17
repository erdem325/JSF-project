package com.jforce.controller;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.RowEditEvent;

import com.jforce.model.KisiBilgiModel;
import com.jforce.model.UserBilgi;
@ManagedBean(name="kisiController")
@ViewScoped
public class KisiController implements Serializable{
	private String name;
	private Long identification;
	private UserBilgi kisiBilgi;
	private boolean addOperation=false;
	private boolean disable=true;
	private boolean visible=true;
	private Map<String,String> iller;
	private Map<String,String> ilceler;
	private Map<String,Map<String,String>> data = new HashMap<String, Map<String,String>>();
	private List<UserBilgi> liste = new ArrayList<>();
	public void kisi() {
		
		KisiController kisiKaydet = new KisiController();
		kisiKaydet.Kaydet(getUserBilgi());
	}
	
	@PostConstruct
	public void ilBilgisi() {
		
		setUserBilgi(new UserBilgi());
		iller = new HashMap<String, String>();
        iller.put("Ýzmir", "Ýzmir");
        iller.put("Ýstanbul", "Ýstanbul");
        iller.put("Ankara", "Ankara");
         
        Map<String,String> ilce = new HashMap<String, String>();
        ilce.put("Karþýyaka", "Karþýyaka");
        ilce.put("Alsancak", "Alsancak");
        ilce.put("Konak", "Konak");
        data.put("Ýzmir", ilce);
         
        ilce = new HashMap<String, String>();
        ilce.put("Üsküdar", "Üsküdar");
        ilce.put("Kadýköy", "Kadýköy");
        ilce.put("Ümraniye", "Ümraniye");
        data.put("Ýstanbul", ilce);
         
        ilce = new HashMap<String, String>();
        ilce.put("Keçiören", "Keçiören");
        ilce.put("Çankaya", "Çankaya");
        ilce.put("Altýndað", "Altýndað");
        data.put("Ankara", ilce);
	}
	
	public void ilSec(){
	        
		if(getUserBilgi().getCity() !=null && !getUserBilgi().getCity().equals(""))
	       	ilceler = data.get(getUserBilgi().getCity());         
	        
		else
	       	ilceler = new HashMap<String, String>();    
	}
	
	public boolean isDisable()
	{
		return disable;
		
	}
	
	public boolean isVisibled(boolean visible)
	{
		return visible;
	}
	
	public void readonly() {
		if(isDisable()) {
			ara();
		}
		else {
			commit();
		}
	}
	
	
	public void commit(){
		
		if(isAddOperation()) {
			kisi();
		}else{
			update();
		}
	}
	
	
	public void yeniKayit() {
		disable=false;
		addOperation=true;
		setUserBilgi(new UserBilgi());
		
	}
	
	
	
	public boolean isAddOperation() {
		return addOperation;
	}
	
	public UserBilgi Kaydet(UserBilgi kisiBilgi) {
			
		try {
			@SuppressWarnings("resource")
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			MongoDatabase dbs = mongoClient.getDatabase("jforcedb");	
			MongoCollection<Document> collection = dbs.getCollection("bilgiler");			
			Document document = new Document("ad", kisiBilgi.getName().trim().toLowerCase());
			document.append("soyad",  kisiBilgi.getSurname() );			
			document.append("tckimlikno",kisiBilgi.getIdentification() );
			document.append("dogumtarihi",  kisiBilgi.getBirthday() );
			document.append("epostaadresi",  kisiBilgi.getEmail()  );
			document.append("ceptelefonuno",  kisiBilgi.getPhonenumber());
			document.append("il",kisiBilgi.getCity());
			document.append("ilce",kisiBilgi.getState());
			collection.insertOne(document);
			System.out.println(document.toString());		
		}
		catch(Exception e) {
			System.out.println("Hata" + e.getMessage());
		}
		return kisiBilgi;
	}
	
	
	public void veriCek(boolean disable, boolean visible) {
		this.disable=disable;
		this.visible=visible;
		try {
			@SuppressWarnings("resource")
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			MongoDatabase dbs = mongoClient.getDatabase("jforcedb");
			MongoCollection<Document> collection = dbs.getCollection("bilgiler");
			BasicDBObject filter = new BasicDBObject();
			filter.put("tckimlikno",getUserBilgi().getIdentification());
			MongoCursor<Document> cursor = collection.find(filter).iterator();
				while(cursor.hasNext()) {		
					Document next =cursor.next();
			    	
			    	UserBilgi kisiBilgi = new UserBilgi();
				    kisiBilgi.setName(next.getString("ad"));
				    kisiBilgi.setSurname(next.getString("soyad")); 
				    kisiBilgi.setIdentification(next.getLong("tckimlikno"));
				    kisiBilgi.setBirthday(next.getString("dogumtarihi"));
				    kisiBilgi.setEmail(next.getString("epostaadresi"));
				    kisiBilgi.setPhonenumber(next.getString("ceptelefonuno"));
				    kisiBilgi.setCity(next.getString("il"));
				    kisiBilgi.setState(next.getString("ilce"));
				    setUserBilgi(kisiBilgi);	  
		    		addOperation=false;
		    		break;
				}
		}
		catch (MongoException e) {
			e.printStackTrace();
		}
	}
	
	
	public void ara() {
		liste = new ArrayList<>();
		
		try {
				@SuppressWarnings("resource")
				MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
				MongoDatabase dbs = mongoClient.getDatabase("jforcedb");
				MongoCollection<Document> collection = dbs.getCollection("bilgiler");
				Bson filter = Filters.or(
		                Filters.eq("ad", name.trim().toLowerCase()), 
		                Filters.eq("tckimlikno",identification)
		                );
				MongoCursor<Document> cursor = collection.find(filter).iterator();
					while(cursor.hasNext()) {		
    			
							Document next = cursor.next();
			    			
			    			UserBilgi kisiBilgi = new UserBilgi();
				    		kisiBilgi.setName(next.getString("ad"));
				    		kisiBilgi.setSurname(next.getString("soyad")); 
				    		kisiBilgi.setIdentification(next.getLong("tckimlikno"));
				    		kisiBilgi.setBirthday(next.getString("dogumtarihi"));
				    		kisiBilgi.setEmail(next.getString("epostaadresi"));
				    		kisiBilgi.setPhonenumber(next.getString("ceptelefonuno"));
				    		kisiBilgi.setCity(next.getString("il"));
				    		kisiBilgi.setState(next.getString("ilce"));
				    		setUserBilgi(kisiBilgi);
				    		liste.add(kisiBilgi);
					}
					
		}
		catch (MongoException e) {
			e.printStackTrace();
		}
	}
	
	
	public String sil(UserBilgi kisiBilgi) {
		
			
			@SuppressWarnings("resource")
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			MongoDatabase dbs = mongoClient.getDatabase("jforcedb");
			MongoCollection<Document> collection = dbs.getCollection("bilgiler");
			BasicDBObject filter = new BasicDBObject();
	        filter.put("tckimlikno",getUserBilgi().getIdentification());    
	        
			collection.deleteOne(filter);
			liste.remove(kisiBilgi);
				
			return null;
	}
	
	
	public void update(){
		try {
			@SuppressWarnings("resource")
			MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
			MongoDatabase dbs = mongoClient.getDatabase("jforcedb");
			MongoCollection<Document> collection = dbs.getCollection("bilgiler");
			BasicDBObject filter = new BasicDBObject();
	        filter.put("tckimlikno",getUserBilgi().getIdentification());
			
			Bson yeni = new Document("ad",getUserBilgi().getName().trim().toLowerCase()).append("soyad",getUserBilgi().getSurname())
					.append("tckimlikno",getUserBilgi().getIdentification()).append("dogumtarihi",getUserBilgi().getBirthday())
					.append("epostaadresi",getUserBilgi().getEmail()).append("ceptelefonuno",getUserBilgi().getPhonenumber())
					.append("il",getUserBilgi().getCity()).append("ilce",getUserBilgi().getState());
			Bson updateDocument = new Document("$set",yeni);
	    	collection.updateMany(filter, updateDocument);
	    	ara();
		}
		catch(MongoException e) {
			e.printStackTrace();
		}
		
	}
	
    public UserBilgi getUserBilgi() {
		return kisiBilgi;
	}
	
    public void setUserBilgi(UserBilgi kisiBilgi) {
		this.kisiBilgi = kisiBilgi;
	}
	

	public UserBilgi getKaydet() {
		return kisiBilgi;
	}
	public void setKaydet(UserBilgi kisiBilgi) {
		this.kisiBilgi = kisiBilgi;
	}

	public List<UserBilgi> getListe() {
		return liste;
	}

	public void setListe(List<UserBilgi> liste) {
		this.liste = liste;
	}
	public Map<String, Map<String, String>> getData() {
        return data;
    }
	
	public Map<String, String> getIller() {
	        return iller;
	}
	public Map<String, String> getIlceler() {
	        return ilceler;
	}

	public Long getIdentification() {
		return identification;
	}

	public void setIdentification(Long identification) {
		this.identification = identification;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	

	

	
	
}
	
	
