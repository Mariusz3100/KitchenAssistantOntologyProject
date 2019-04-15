import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class JsonToOwl {
	public static final String inheritRelevance="inherit";
	
	public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException {
		String wholeJson = getJsonDataFromFile();
		JSONObject jsonObj=new JSONObject(wholeJson);
		try {
			JSONArray jsonCategoryArray = (JSONArray)jsonObj.get("categories");	

			JSONObject jsonCategory =(JSONObject) jsonCategoryArray.get(0);
			
			ShopComCategory parseJsonCategory = parseJsonCategory(jsonCategory,null);

			System.out.println();
		}catch(JSONException e) {
			e.printStackTrace();
		}
		

	}

	private static String getJsonDataFromFile() throws FileNotFoundException, IOException {
		File jsonFile=new File("src/main/resources/categoriesReady");
		BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(jsonFile)));
		
		String line="";
		StringBuilder sb=new StringBuilder();
		while((line=br.readLine())!=null) {
			sb.append(line);
		}
		String wholeJson=sb.toString();
		return wholeJson;
	}

	private static ShopComCategory parseJsonCategory(JSONObject jsonCategory,ShopComCategory parent) {
		String name = (String)jsonCategory.getString("name");
		String id = (String)jsonCategory.getString("id");
		Integer relevance = getOrCalculateRelevance(jsonCategory, parent);
		ShopComCategory resultCat=new ShopComCategory(name, id, id, relevance);
		JSONArray jsonCategoryArray = (JSONArray)jsonCategory.get("subCategories");	

		for(int i=0;i<jsonCategoryArray.length();i++) {
			JSONObject innerJsonCategory =(JSONObject) jsonCategoryArray.get(i);
			ShopComCategory innerCategory=parseJsonCategory(innerJsonCategory,resultCat);
			innerCategory.setParentCategory(resultCat);
			resultCat.addChildCategory(innerCategory);
		}
		return resultCat;
		
	}

	private static Integer getOrCalculateRelevance(JSONObject jsonCategory, ShopComCategory parent) {
		Integer relevance=0;
		if(jsonCategory.has("specialRelevance")) {
			String specialRelevance = jsonCategory.getString("specialRelevance");
			if(inheritRelevance.equals(specialRelevance)&&parent!=null&&parent.getRelevance()>0) {
				relevance=parent.getRelevance();
			}else {
				if(jsonCategory.has("relevance")) {
					 relevance= (Integer)jsonCategory.getInt("relevance");
				}
			}
		}else {
			 relevance= (Integer)jsonCategory.getInt("relevance");
		}
		return relevance;
	}
}
