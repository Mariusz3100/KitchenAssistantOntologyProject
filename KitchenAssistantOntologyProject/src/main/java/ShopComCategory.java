import java.util.ArrayList;
import java.util.List;

public class ShopComCategory {
	

	
	private String name;
	private String basicId;
	private String compoundId;
	private String parentingPrefix;

   
	public String getParentingPrefix() {
		return parentingPrefix;
	}


	public void setParentingPrefix(String parentingPrefix) {
		this.parentingPrefix = parentingPrefix;
	}


	private int relevance;
    private ShopComCategory parentCategory;
    private List<ShopComCategory> childCategories;

    
    public List<ShopComCategory> getChildCategories() {
		return childCategories;
	}


	public void addChildCategory(ShopComCategory child) {
		if(childCategories==null)
			childCategories=new ArrayList<ShopComCategory>();
		
		childCategories.add(child);
	}


	public ShopComCategory(String name, String basicId, String compoundId, int relevance) {
		super();
		this.name = name;
		this.basicId = basicId;
		this.compoundId = compoundId;
		this.relevance = relevance;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getBasicId() {
		return basicId;
	}


	public void setBasicId(String basicId) {
		this.basicId = basicId;
	}


	public String getCompoundId() {
		return compoundId;
	}


	public void setCompoundId(String compoundId) {
		this.compoundId = compoundId;
	}


	public int getRelevance() {
		return relevance;
	}


	public void setRelevance(int relevance) {
		this.relevance = relevance;
	}


	public ShopComCategory getParentCategory() {
		return parentCategory;
	}


	public void setParentCategory(ShopComCategory parentCategory) {
		this.parentCategory = parentCategory;
	}
}
