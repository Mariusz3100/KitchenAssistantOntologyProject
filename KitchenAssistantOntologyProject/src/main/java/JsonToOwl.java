import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class JsonToOwl {
	public static final String inheritRelevance="inherit";
	public static final String basePath="/home/mariusz/development/mgr/workspaces/ontologyWorkspace";
	public static Map<String,String> idToParentTranslationMap=new HashMap<String,String>();
	public static Map<String,String> parentToIdTranslationMap=new HashMap<String,String>();


	
	
	
	static {
//		idToParentTranslationMap.put("2-2144059", "3-7677_a");
//		idToParentTranslationMap.put("2-2143970", "3-7669_a");
//		idToParentTranslationMap.put("2-2143985", "3-7670_a");
//		idToParentTranslationMap.put("2-2143942", "3-7667_a");
//		idToParentTranslationMap.put("2-2144024", "3-7674_a");
//		idToParentTranslationMap.put("2-2143994", "3-7671_a");
//		idToParentTranslationMap.put("2-2144014", "");
//		idToParentTranslationMap.put("", "");
//		idToParentTranslationMap.put("", "");
//		
		
		
		
	}
	
	public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException, IOException {
		String wholeJson = getJsonDataFromFile();
		JSONObject jsonObj=new JSONObject(wholeJson);
		try {
			JSONArray jsonCategoryArray = (JSONArray)jsonObj.get("categories");	

			JSONObject jsonCategory =(JSONObject) jsonCategoryArray.get(0);
			
			ShopComCategory parseJsonCategory = parseJsonCategory(jsonCategory,null);

			
			File target=new File(basePath+"/readyCategories.owl");
			target.createNewFile();
			OntologyWriter.createNewOntology(parseJsonCategory, target);
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
		String basicId,comcoundId=id;
		
		if(parent!=null) {
			if(id!=null) {
				String[] split = id.split(":");
				if(split.length<2) {
					
					basicId=id;
				}else {
					String firstPart = split[0];//parentToIdTranslationMap.get();
					basicId=split[1];
					if(parent.getParentingPrefix()==null) {
						parent.setParentingPrefix(firstPart);
						String firstPartFromMap = parentToIdTranslationMap.get(firstPart);
						if(firstPartFromMap==null) {
							parentToIdTranslationMap.put(firstPart,parent.getBasicId());
							idToParentTranslationMap.put(parent.getBasicId(),firstPart);

						}else {
							if(!parent.getBasicId().equals(firstPartFromMap)) {
								System.err.println("different parent ids for "+id);

							}
						}
					}else {
						String firstPartFromMap = parentToIdTranslationMap.get(firstPart);
						if(firstPartFromMap==null) {
							parentToIdTranslationMap.put(firstPart,parent.getBasicId());
							idToParentTranslationMap.put(parent.getBasicId(),firstPart);
						}else {
							if(!firstPart.equals(parent.getParentingPrefix())) {
								System.err.println("different parent ids for "+id);
							}else {
								
							}
						}
					}
				}
				
				
				
				
			}
		}
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
