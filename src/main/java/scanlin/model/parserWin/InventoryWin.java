package scanlin.model.parserWin;

import scanlin.model.Storage;

import java.util.ArrayList;
import java.util.List;

public class InventoryWin extends Storage {
    private String id;
    private String description;
    private CriteriaWin criteria;
    private String product;
    private String title;
    private List<String> platforms;

    public InventoryWin() {
        this.criteria = new CriteriaWin();
        this.platforms = new ArrayList<>();
    }

    public InventoryWin(String id, String description, CriteriaWin criteria, String product,
                        String title, List<String> platforms) {
        this.id = id;
        this.description = description;
        this.criteria = criteria;
        this.product = product;
        this.title = title;
        this.platforms = platforms;
    }

    // Геттеры и сеттеры для всех полей
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public CriteriaWin getCriteria() {
        return this.criteria;
    }

    public void setCriteria(CriteriaWin criteria) {
        this.criteria = criteria;
    }

    public String getProduct() { return product; }

    public void setProduct(String product) { this.product = product; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<String> getPlatforms() { return platforms; }
    public void setPlatforms(List<String> platforms) { this.platforms = platforms;}
    public void addPlatform(String platform) {this.platforms.add(platform);}

    @Override
    public String toString() {
        return "Inventory{" +
                "id='" + id + '\'' + '\n' +
                "description='" + description + '\'' + '\n' +
                "product='" + product + '\'' + '\n' +
                "platforms=" + platforms.toString() + "\n" +
                "title='" + title + '\'' + '\n' + criteria.toString() +
                '}';
    }
}
