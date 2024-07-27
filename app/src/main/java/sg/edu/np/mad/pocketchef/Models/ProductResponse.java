// ProductResponse.java

// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation

package sg.edu.np.mad.pocketchef.Models;
import java.util.List;
import java.util.Map;

public class ProductResponse {
    private Product product;
    private String code;
    private String statusVerbose;
    private long status;

    public Product getProduct() { return product; }
    public void setProduct(Product value) { this.product = value; }

    public String getCode() { return code; }
    public void setCode(String value) { this.code = value; }

    public String getStatusVerbose() { return statusVerbose; }
    public void setStatusVerbose(String value) { this.statusVerbose = value; }

    public long getStatus() { return status; }
    public void setStatus(long value) { this.status = value; }

   public static class Product {
        private List<String> ecoscoreTags;
        private String allergensFromIngredients;
        private String product_quantity_unit;
        private List<Object> addedCountriesTags;
        private CategoriesProperties nutriments;
        private List<String> languagesHierarchy;
        private List<Object> packagingShapesTags;
        private String productid;
        private List<String> codesTags;
        private String imageSmallurl;
        private CategoriesProperties nutrientLevels;
        private List<String> brandsTags;
        private Images images;
        private String image_url;
        private List<String> lastImageDatesTags;
        private long popularityKey;
        private String countries;
        private List<Object> packagingMaterialsTags;
        private List<String> entryDatesTags;
        private String nutriscoreVersion;
        private long lastUpdatedT;
        private List<Object> tracesTags;
        private String novaGroupError;
        private String imageFrontSmallurl;
        private long nutritionScoreWarningNoFruitsVegetablesNuts;
        private String nutritionGradeFr;
        private List<String> dataQualityTags;
        private List<String> novaGroupsTags;
        private List<Object> checkersTags;
        private List<Object> mainCountriesTags;
        private List<String> categoriesPropertiesTags;
        private List<String> pnnsGroups1_Tags;
        private LanguagesCodes languagesCodes;
        private List<String> statesTags;
        private List<Object> allergensTags;
        private String product_quantity;
        private String tracesFromIngredients;
        private String nutritionDataPreparedPer;
        private CategoriesProperties packagingsMaterials;
        private List<String> nutriscore2023_Tags;
        private List<Object> packagingRecyclingTags;
        private List<Object> nutrientLevelsTags;
        private String interfaceVersionCreated;
        private String creator;
        private String brands;
        private Languages languages;
        private EcoscoreData ecoscoreData;
        private String tracesFromUser;
        private List<String> countriesTags;
        private String novaGroupDebug;
        private List<String> editorsTags;
        private long createdT;
        private String lc;
        private String nutriscoreGrade;
        private SelectedImages selectedImages;
        private List<Object> dataQualityErrorsTags;
        private long complete;
        private String nutritionGrades;
        private String pnnsGroups1;
        private String pnnsGroups2;
        private List<String> nutriscoreTags;
        private List<Object> foodGroupsTags;
        private List<Object> correctorsTags;
        private List<Object> removedCountriesTags;
        private List<String> dataQualityWarningsTags;
        private String imageFrontThumburl;
        private String nutritionScoreDebug;
        private double completeness;
        private List<Object> packagings;
        private List<String> languagesTags;
        private List<String> keywords;
        private Map<String, Nutriscore> nutriscore;
        private String productNameEn;
        private CategoriesProperties categoriesProperties;
        private long rev;
        private String ecoscoreGrade;
        private List<String> informersTags;
        private String allergensFromUser;
        private String lastEditor;
        private String traces;
        private List<Object> tracesHierarchy;
        private String product_name;
        private List<String> photographersTags;
        private List<String> statesHierarchy;
        private String id;
        private String interfaceVersionModified;
        private String code;
        private long nutritionScoreWarningNoFiber;
        private List<String> miscTags;
        private List<Object> unknownNutrientsTags;
        private List<String> lastEditDatesTags;
        private String states;
        private List<Object> dataQualityBugsTags;
        private String nutritionDataPer;
        private String ingredientsLc;
        private String lang;
        private String imageFronturl;
        private List<String> pnnsGroups2_Tags;
        private List<String> countriesHierarchy;
        private String quantity;
        private List<String> dataQualityInfoTags;
        private long lastModifiedT;
        private String lastModifiedBy;
        private String imageThumburl;
        private String allergens;
        private List<String> nutritionGradesTags;
        private long lastImageT;
        private long nutritionScoreBeverage;
        private String maxImgid;
        private List<String> nutriscore2021_Tags;
        private List<Object> weighersTags;
        private List<Object> allergensHierarchy;

        public List<String> getEcoscoreTags() { return ecoscoreTags; }
        public void setEcoscoreTags(List<String> value) { this.ecoscoreTags = value; }

        public String getAllergensFromIngredients() { return allergensFromIngredients; }
        public void setAllergensFromIngredients(String value) { this.allergensFromIngredients = value; }

        public String getProduct_quantity_unit() { return product_quantity_unit; }
        public void setProduct_quantity_unit(String value) { this.product_quantity_unit = value; }

        public List<Object> getAddedCountriesTags() { return addedCountriesTags; }
        public void setAddedCountriesTags(List<Object> value) { this.addedCountriesTags = value; }

        public CategoriesProperties getNutriments() { return nutriments; }
        public void setNutriments(CategoriesProperties value) { this.nutriments = value; }

        public List<String> getLanguagesHierarchy() { return languagesHierarchy; }
        public void setLanguagesHierarchy(List<String> value) { this.languagesHierarchy = value; }

        public List<Object> getPackagingShapesTags() { return packagingShapesTags; }
        public void setPackagingShapesTags(List<Object> value) { this.packagingShapesTags = value; }

        public String getProductid() { return productid; }
        public void setProductid(String value) { this.productid = value; }

        public List<String> getCodesTags() { return codesTags; }
        public void setCodesTags(List<String> value) { this.codesTags = value; }

        public String getImageSmallurl() { return imageSmallurl; }
        public void setImageSmallurl(String value) { this.imageSmallurl = value; }

        public CategoriesProperties getNutrientLevels() { return nutrientLevels; }
        public void setNutrientLevels(CategoriesProperties value) { this.nutrientLevels = value; }

        public List<String> getBrandsTags() { return brandsTags; }
        public void setBrandsTags(List<String> value) { this.brandsTags = value; }

        public Images getImages() { return images; }
        public void setImages(Images value) { this.images = value; }

        public String getImage_url() { return image_url; }
        public void setImage_url(String value) { this.image_url = value; }

        public List<String> getLastImageDatesTags() { return lastImageDatesTags; }
        public void setLastImageDatesTags(List<String> value) { this.lastImageDatesTags = value; }

        public long getPopularityKey() { return popularityKey; }
        public void setPopularityKey(long value) { this.popularityKey = value; }

        public String getCountries() { return countries; }
        public void setCountries(String value) { this.countries = value; }

        public List<Object> getPackagingMaterialsTags() { return packagingMaterialsTags; }
        public void setPackagingMaterialsTags(List<Object> value) { this.packagingMaterialsTags = value; }

        public List<String> getEntryDatesTags() { return entryDatesTags; }
        public void setEntryDatesTags(List<String> value) { this.entryDatesTags = value; }

        public String getNutriscoreVersion() { return nutriscoreVersion; }
        public void setNutriscoreVersion(String value) { this.nutriscoreVersion = value; }

        public long getLastUpdatedT() { return lastUpdatedT; }
        public void setLastUpdatedT(long value) { this.lastUpdatedT = value; }

        public List<Object> getTracesTags() { return tracesTags; }
        public void setTracesTags(List<Object> value) { this.tracesTags = value; }

        public String getNovaGroupError() { return novaGroupError; }
        public void setNovaGroupError(String value) { this.novaGroupError = value; }

        public String getImageFrontSmallurl() { return imageFrontSmallurl; }
        public void setImageFrontSmallurl(String value) { this.imageFrontSmallurl = value; }

        public long getNutritionScoreWarningNoFruitsVegetablesNuts() { return nutritionScoreWarningNoFruitsVegetablesNuts; }
        public void setNutritionScoreWarningNoFruitsVegetablesNuts(long value) { this.nutritionScoreWarningNoFruitsVegetablesNuts = value; }

        public String getNutritionGradeFr() { return nutritionGradeFr; }
        public void setNutritionGradeFr(String value) { this.nutritionGradeFr = value; }

        public List<String> getDataQualityTags() { return dataQualityTags; }
        public void setDataQualityTags(List<String> value) { this.dataQualityTags = value; }

        public List<String> getNovaGroupsTags() { return novaGroupsTags; }
        public void setNovaGroupsTags(List<String> value) { this.novaGroupsTags = value; }

        public List<Object> getCheckersTags() { return checkersTags; }
        public void setCheckersTags(List<Object> value) { this.checkersTags = value; }

        public List<Object> getMainCountriesTags() { return mainCountriesTags; }
        public void setMainCountriesTags(List<Object> value) { this.mainCountriesTags = value; }

        public List<String> getCategoriesPropertiesTags() { return categoriesPropertiesTags; }
        public void setCategoriesPropertiesTags(List<String> value) { this.categoriesPropertiesTags = value; }

        public List<String> getPnnsGroups1Tags() { return pnnsGroups1_Tags; }
        public void setPnnsGroups1Tags(List<String> value) { this.pnnsGroups1_Tags = value; }

        public LanguagesCodes getLanguagesCodes() { return languagesCodes; }
        public void setLanguagesCodes(LanguagesCodes value) { this.languagesCodes = value; }

        public List<String> getStatesTags() { return statesTags; }
        public void setStatesTags(List<String> value) { this.statesTags = value; }

        public List<Object> getAllergensTags() { return allergensTags; }
        public void setAllergensTags(List<Object> value) { this.allergensTags = value; }

        public String getProduct_quantity() { return product_quantity; }
        public void setProduct_quantity(String value) { this.product_quantity = value; }

        public String getTracesFromIngredients() { return tracesFromIngredients; }
        public void setTracesFromIngredients(String value) { this.tracesFromIngredients = value; }

        public String getNutritionDataPreparedPer() { return nutritionDataPreparedPer; }
        public void setNutritionDataPreparedPer(String value) { this.nutritionDataPreparedPer = value; }

        public CategoriesProperties getPackagingsMaterials() { return packagingsMaterials; }
        public void setPackagingsMaterials(CategoriesProperties value) { this.packagingsMaterials = value; }

        public List<String> getNutriscore2023Tags() { return nutriscore2023_Tags; }
        public void setNutriscore2023Tags(List<String> value) { this.nutriscore2023_Tags = value; }

        public List<Object> getPackagingRecyclingTags() { return packagingRecyclingTags; }
        public void setPackagingRecyclingTags(List<Object> value) { this.packagingRecyclingTags = value; }

        public List<Object> getNutrientLevelsTags() { return nutrientLevelsTags; }
        public void setNutrientLevelsTags(List<Object> value) { this.nutrientLevelsTags = value; }

        public String getInterfaceVersionCreated() { return interfaceVersionCreated; }
        public void setInterfaceVersionCreated(String value) { this.interfaceVersionCreated = value; }

        public String getCreator() { return creator; }
        public void setCreator(String value) { this.creator = value; }

        public String getBrands() { return brands; }
        public void setBrands(String value) { this.brands = value; }

        public Languages getLanguages() { return languages; }
        public void setLanguages(Languages value) { this.languages = value; }

        public EcoscoreData getEcoscoreData() { return ecoscoreData; }
        public void setEcoscoreData(EcoscoreData value) { this.ecoscoreData = value; }

        public String getTracesFromUser() { return tracesFromUser; }
        public void setTracesFromUser(String value) { this.tracesFromUser = value; }

        public List<String> getCountriesTags() { return countriesTags; }
        public void setCountriesTags(List<String> value) { this.countriesTags = value; }

        public String getNovaGroupDebug() { return novaGroupDebug; }
        public void setNovaGroupDebug(String value) { this.novaGroupDebug = value; }

        public List<String> getEditorsTags() { return editorsTags; }
        public void setEditorsTags(List<String> value) { this.editorsTags = value; }

        public long getCreatedT() { return createdT; }
        public void setCreatedT(long value) { this.createdT = value; }

        public String getLc() { return lc; }
        public void setLc(String value) { this.lc = value; }

        public String getNutriscoreGrade() { return nutriscoreGrade; }
        public void setNutriscoreGrade(String value) { this.nutriscoreGrade = value; }

        public SelectedImages getSelectedImages() { return selectedImages; }
        public void setSelectedImages(SelectedImages value) { this.selectedImages = value; }

        public List<Object> getDataQualityErrorsTags() { return dataQualityErrorsTags; }
        public void setDataQualityErrorsTags(List<Object> value) { this.dataQualityErrorsTags = value; }

        public long getComplete() { return complete; }
        public void setComplete(long value) { this.complete = value; }

        public String getNutritionGrades() { return nutritionGrades; }
        public void setNutritionGrades(String value) { this.nutritionGrades = value; }

        public String getPnnsGroups1() { return pnnsGroups1; }
        public void setPnnsGroups1(String value) { this.pnnsGroups1 = value; }

        public String getPnnsGroups2() { return pnnsGroups2; }
        public void setPnnsGroups2(String value) { this.pnnsGroups2 = value; }

        public List<String> getNutriscoreTags() { return nutriscoreTags; }
        public void setNutriscoreTags(List<String> value) { this.nutriscoreTags = value; }

        public List<Object> getFoodGroupsTags() { return foodGroupsTags; }
        public void setFoodGroupsTags(List<Object> value) { this.foodGroupsTags = value; }

        public List<Object> getCorrectorsTags() { return correctorsTags; }
        public void setCorrectorsTags(List<Object> value) { this.correctorsTags = value; }

        public List<Object> getRemovedCountriesTags() { return removedCountriesTags; }
        public void setRemovedCountriesTags(List<Object> value) { this.removedCountriesTags = value; }

        public List<String> getDataQualityWarningsTags() { return dataQualityWarningsTags; }
        public void setDataQualityWarningsTags(List<String> value) { this.dataQualityWarningsTags = value; }

        public String getImageFrontThumburl() { return imageFrontThumburl; }
        public void setImageFrontThumburl(String value) { this.imageFrontThumburl = value; }

        public String getNutritionScoreDebug() { return nutritionScoreDebug; }
        public void setNutritionScoreDebug(String value) { this.nutritionScoreDebug = value; }

        public double getCompleteness() { return completeness; }
        public void setCompleteness(double value) { this.completeness = value; }

        public List<Object> getPackagings() { return packagings; }
        public void setPackagings(List<Object> value) { this.packagings = value; }

        public List<String> getLanguagesTags() { return languagesTags; }
        public void setLanguagesTags(List<String> value) { this.languagesTags = value; }

        public List<String> getKeywords() { return keywords; }
        public void setKeywords(List<String> value) { this.keywords = value; }

        public Map<String, Nutriscore> getNutriscore() { return nutriscore; }
        public void setNutriscore(Map<String, Nutriscore> value) { this.nutriscore = value; }

        public String getProductNameEn() { return productNameEn; }
        public void setProductNameEn(String value) { this.productNameEn = value; }

        public CategoriesProperties getCategoriesProperties() { return categoriesProperties; }
        public void setCategoriesProperties(CategoriesProperties value) { this.categoriesProperties = value; }

        public long getRev() { return rev; }
        public void setRev(long value) { this.rev = value; }

        public String getEcoscoreGrade() { return ecoscoreGrade; }
        public void setEcoscoreGrade(String value) { this.ecoscoreGrade = value; }

        public List<String> getInformersTags() { return informersTags; }
        public void setInformersTags(List<String> value) { this.informersTags = value; }

        public String getAllergensFromUser() { return allergensFromUser; }
        public void setAllergensFromUser(String value) { this.allergensFromUser = value; }

        public String getLastEditor() { return lastEditor; }
        public void setLastEditor(String value) { this.lastEditor = value; }

        public String getTraces() { return traces; }
        public void setTraces(String value) { this.traces = value; }

        public List<Object> getTracesHierarchy() { return tracesHierarchy; }
        public void setTracesHierarchy(List<Object> value) { this.tracesHierarchy = value; }

        public String getProduct_name() { return product_name; }
        public void setProduct_name(String value) { this.product_name = value; }

        public List<String> getPhotographersTags() { return photographersTags; }
        public void setPhotographersTags(List<String> value) { this.photographersTags = value; }

        public List<String> getStatesHierarchy() { return statesHierarchy; }
        public void setStatesHierarchy(List<String> value) { this.statesHierarchy = value; }

        public String getid() { return id; }
        public void setid(String value) { this.id = value; }

        public String getInterfaceVersionModified() { return interfaceVersionModified; }
        public void setInterfaceVersionModified(String value) { this.interfaceVersionModified = value; }

        public String getCode() { return code; }
        public void setCode(String value) { this.code = value; }

        public long getNutritionScoreWarningNoFiber() { return nutritionScoreWarningNoFiber; }
        public void setNutritionScoreWarningNoFiber(long value) { this.nutritionScoreWarningNoFiber = value; }

        public List<String> getMiscTags() { return miscTags; }
        public void setMiscTags(List<String> value) { this.miscTags = value; }

        public List<Object> getUnknownNutrientsTags() { return unknownNutrientsTags; }
        public void setUnknownNutrientsTags(List<Object> value) { this.unknownNutrientsTags = value; }

        public List<String> getLastEditDatesTags() { return lastEditDatesTags; }
        public void setLastEditDatesTags(List<String> value) { this.lastEditDatesTags = value; }

        public String getStates() { return states; }
        public void setStates(String value) { this.states = value; }

        public List<Object> getDataQualityBugsTags() { return dataQualityBugsTags; }
        public void setDataQualityBugsTags(List<Object> value) { this.dataQualityBugsTags = value; }

        public String getNutritionDataPer() { return nutritionDataPer; }
        public void setNutritionDataPer(String value) { this.nutritionDataPer = value; }

        public String getIngredientsLc() { return ingredientsLc; }
        public void setIngredientsLc(String value) { this.ingredientsLc = value; }

        public String getLang() { return lang; }
        public void setLang(String value) { this.lang = value; }

        public String getImageFronturl() { return imageFronturl; }
        public void setImageFronturl(String value) { this.imageFronturl = value; }

        public List<String> getPnnsGroups2Tags() { return pnnsGroups2_Tags; }
        public void setPnnsGroups2Tags(List<String> value) { this.pnnsGroups2_Tags = value; }

        public List<String> getCountriesHierarchy() { return countriesHierarchy; }
        public void setCountriesHierarchy(List<String> value) { this.countriesHierarchy = value; }

        public String getQuantity() { return quantity; }
        public void setQuantity(String value) { this.quantity = value; }

        public List<String> getDataQualityInfoTags() { return dataQualityInfoTags; }
        public void setDataQualityInfoTags(List<String> value) { this.dataQualityInfoTags = value; }

        public long getLastModifiedT() { return lastModifiedT; }
        public void setLastModifiedT(long value) { this.lastModifiedT = value; }

        public String getLastModifiedBy() { return lastModifiedBy; }
        public void setLastModifiedBy(String value) { this.lastModifiedBy = value; }

        public String getImageThumburl() { return imageThumburl; }
        public void setImageThumburl(String value) { this.imageThumburl = value; }

        public String getAllergens() { return allergens; }
        public void setAllergens(String value) { this.allergens = value; }

        public List<String> getNutritionGradesTags() { return nutritionGradesTags; }
        public void setNutritionGradesTags(List<String> value) { this.nutritionGradesTags = value; }

        public long getLastImageT() { return lastImageT; }
        public void setLastImageT(long value) { this.lastImageT = value; }

        public long getNutritionScoreBeverage() { return nutritionScoreBeverage; }
        public void setNutritionScoreBeverage(long value) { this.nutritionScoreBeverage = value; }

        public String getMaxImgid() { return maxImgid; }
        public void setMaxImgid(String value) { this.maxImgid = value; }

        public List<String> getNutriscore2021Tags() { return nutriscore2021_Tags; }
        public void setNutriscore2021Tags(List<String> value) { this.nutriscore2021_Tags = value; }

        public List<Object> getWeighersTags() { return weighersTags; }
        public void setWeighersTags(List<Object> value) { this.weighersTags = value; }

        public List<Object> getAllergensHierarchy() { return allergensHierarchy; }
        public void setAllergensHierarchy(List<Object> value) { this.allergensHierarchy = value; }
    }

    static  class CategoriesProperties {
    }

    static class EcoscoreData {
        private long missingKeyData;
        private Adjustments adjustments;
        private CategoriesProperties scores;
        private Missing missing;
        private long missingAgribalyseMatchWarning;
        private Agribalyse agribalyse;
        private String status;

        public long getMissingKeyData() { return missingKeyData; }
        public void setMissingKeyData(long value) { this.missingKeyData = value; }

        public Adjustments getAdjustments() { return adjustments; }
        public void setAdjustments(Adjustments value) { this.adjustments = value; }

        public CategoriesProperties getScores() { return scores; }
        public void setScores(CategoriesProperties value) { this.scores = value; }

        public Missing getMissing() { return missing; }
        public void setMissing(Missing value) { this.missing = value; }

        public long getMissingAgribalyseMatchWarning() { return missingAgribalyseMatchWarning; }
        public void setMissingAgribalyseMatchWarning(long value) { this.missingAgribalyseMatchWarning = value; }

        public Agribalyse getAgribalyse() { return agribalyse; }
        public void setAgribalyse(Agribalyse value) { this.agribalyse = value; }

        public String getStatus() { return status; }
        public void setStatus(String value) { this.status = value; }
    }

// Adjustments.java

// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation

    static class Adjustments {
        private OriginsOfIngredients originsOfIngredients;
        private Packaging packaging;
        private Agribalyse threatenedSpecies;
        private ProductionSystem productionSystem;

        public OriginsOfIngredients getOriginsOfIngredients() { return originsOfIngredients; }
        public void setOriginsOfIngredients(OriginsOfIngredients value) { this.originsOfIngredients = value; }

        public Packaging getPackaging() { return packaging; }
        public void setPackaging(Packaging value) { this.packaging = value; }

        public Agribalyse getThreatenedSpecies() { return threatenedSpecies; }
        public void setThreatenedSpecies(Agribalyse value) { this.threatenedSpecies = value; }

        public ProductionSystem getProductionSystem() { return productionSystem; }
        public void setProductionSystem(ProductionSystem value) { this.productionSystem = value; }
    }

// OriginsOfIngredients.java

// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation




    static class OriginsOfIngredients {
        private long epiScore;
        private List<String> originsFromOriginsField;
        private Map<String, Long> transportationScores;
        private Map<String, Long> transportationValues;
        private List<AggregatedOrigin> aggregatedOrigins;
        private long transportationValue;
        private Map<String, Long> values;
        private String warning;
        private long epiValue;
        private List<String> originsFromCategories;
        private long value;
        private long transportationScore;

        public long getEpiScore() { return epiScore; }
        public void setEpiScore(long value) { this.epiScore = value; }

        public List<String> getOriginsFromOriginsField() { return originsFromOriginsField; }
        public void setOriginsFromOriginsField(List<String> value) { this.originsFromOriginsField = value; }

        public Map<String, Long> getTransportationScores() { return transportationScores; }
        public void setTransportationScores(Map<String, Long> value) { this.transportationScores = value; }

        public Map<String, Long> getTransportationValues() { return transportationValues; }
        public void setTransportationValues(Map<String, Long> value) { this.transportationValues = value; }

        public List<AggregatedOrigin> getAggregatedOrigins() { return aggregatedOrigins; }
        public void setAggregatedOrigins(List<AggregatedOrigin> value) { this.aggregatedOrigins = value; }

        public long getTransportationValue() { return transportationValue; }
        public void setTransportationValue(long value) { this.transportationValue = value; }

        public Map<String, Long> getValues() { return values; }
        public void setValues(Map<String, Long> value) { this.values = value; }

        public String getWarning() { return warning; }
        public void setWarning(String value) { this.warning = value; }

        public long getEpiValue() { return epiValue; }
        public void setEpiValue(long value) { this.epiValue = value; }

        public List<String> getOriginsFromCategories() { return originsFromCategories; }
        public void setOriginsFromCategories(List<String> value) { this.originsFromCategories = value; }

        public long getValue() { return value; }
        public void setValue(long value) { this.value = value; }

        public long getTransportationScore() { return transportationScore; }
        public void setTransportationScore(long value) { this.transportationScore = value; }
    }

// AggregatedOrigin.java

// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation

    static class AggregatedOrigin {
        private String epiScore;
        private String origin;
        private long percent;

        public String getEpiScore() { return epiScore; }
        public void setEpiScore(String value) { this.epiScore = value; }

        public String getOrigin() { return origin; }
        public void setOrigin(String value) { this.origin = value; }

        public long getPercent() { return percent; }
        public void setPercent(long value) { this.percent = value; }
    }

// Packaging.java

// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation

    static class Packaging {
        private long nonRecyclableAndNonBiodegradableMaterials;
        private String warning;
        private long value;

        public long getNonRecyclableAndNonBiodegradableMaterials() { return nonRecyclableAndNonBiodegradableMaterials; }
        public void setNonRecyclableAndNonBiodegradableMaterials(long value) { this.nonRecyclableAndNonBiodegradableMaterials = value; }

        public String getWarning() { return warning; }
        public void setWarning(String value) { this.warning = value; }

        public long getValue() { return value; }
        public void setValue(long value) { this.value = value; }
    }

// ProductionSystem.java

// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation

    static  class ProductionSystem {
        private String warning;
        private long value;
        private List<Object> labels;

        public String getWarning() { return warning; }
        public void setWarning(String value) { this.warning = value; }

        public long getValue() { return value; }
        public void setValue(long value) { this.value = value; }

        public List<Object> getLabels() { return labels; }
        public void setLabels(List<Object> value) { this.labels = value; }
    }

    static class Agribalyse {
        private String warning;

        public String getWarning() { return warning; }
        public void setWarning(String value) { this.warning = value; }
    }


    static class Missing {
        private long packagings;
        private long ingredients;
        private long origins;
        private long categories;
        private long labels;

        public long getPackagings() { return packagings; }
        public void setPackagings(long value) { this.packagings = value; }

        public long getIngredients() { return ingredients; }
        public void setIngredients(long value) { this.ingredients = value; }

        public long getOrigins() { return origins; }
        public void setOrigins(long value) { this.origins = value; }

        public long getCategories() { return categories; }
        public void setCategories(long value) { this.categories = value; }

        public long getLabels() { return labels; }
        public void setLabels(long value) { this.labels = value; }
    }


    static class Images {
        private The1 the1;
        private FrontEn frontEn;

        public The1 getThe1() { return the1; }
        public void setThe1(The1 value) { this.the1 = value; }

        public FrontEn getFrontEn() { return frontEn; }
        public void setFrontEn(FrontEn value) { this.frontEn = value; }
    }

    static class FrontEn {
        private String coordinatesImageSize;
        private String rev;
        private String imgid;
        private Sizes sizes;
        private String y1;
        private long angle;
        private String x1;
        private String y2;
        private String geometry;
        private String x2;

        public String getCoordinatesImageSize() { return coordinatesImageSize; }
        public void setCoordinatesImageSize(String value) { this.coordinatesImageSize = value; }

        public String getRev() { return rev; }
        public void setRev(String value) { this.rev = value; }

        public String getImgid() { return imgid; }
        public void setImgid(String value) { this.imgid = value; }

        public Sizes getSizes() { return sizes; }
        public void setSizes(Sizes value) { this.sizes = value; }

        public String getY1() { return y1; }
        public void setY1(String value) { this.y1 = value; }

        public long getAngle() { return angle; }
        public void setAngle(long value) { this.angle = value; }

        public String getX1() { return x1; }
        public void setX1(String value) { this.x1 = value; }

        public String getY2() { return y2; }
        public void setY2(String value) { this.y2 = value; }

        public String getGeometry() { return geometry; }
        public void setGeometry(String value) { this.geometry = value; }

        public String getX2() { return x2; }
        public void setX2(String value) { this.x2 = value; }
    }


    static class Sizes {
        private The100 the100;
        private The100 the400;
        private The100 full;
        private The100 the200;

        public The100 getThe100() { return the100; }
        public void setThe100(The100 value) { this.the100 = value; }

        public The100 getThe400() { return the400; }
        public void setThe400(The100 value) { this.the400 = value; }

        public The100 getFull() { return full; }
        public void setFull(The100 value) { this.full = value; }

        public The100 getThe200() { return the200; }
        public void setThe200(The100 value) { this.the200 = value; }
    }

    static class The100 {
        private long w;
        private long h;

        public long getW() { return w; }
        public void setW(long value) { this.w = value; }

        public long getH() { return h; }
        public void setH(long value) { this.h = value; }
    }


    static class The1 {
        private Sizes sizes;
        private String uploader;
        private long uploadedT;

        public Sizes getSizes() { return sizes; }
        public void setSizes(Sizes value) { this.sizes = value; }

        public String getUploader() { return uploader; }
        public void setUploader(String value) { this.uploader = value; }

        public long getUploadedT() { return uploadedT; }
        public void setUploadedT(long value) { this.uploadedT = value; }
    }


    static   class Languages {
        private long enEnglish;

        public long getEnEnglish() { return enEnglish; }
        public void setEnEnglish(long value) { this.enEnglish = value; }
    }


    static  class LanguagesCodes {
        private long en;

        public long getEn() { return en; }
        public void setEn(long value) { this.en = value; }
    }

    static class Nutriscore {
        private Data data;
        private long nutriscoreApplicable;
        private String grade;
        private long categoryAvailable;
        private long nutriscoreComputed;
        private long nutrientsAvailable;

        public Data getData() { return data; }
        public void setData(Data value) { this.data = value; }

        public long getNutriscoreApplicable() { return nutriscoreApplicable; }
        public void setNutriscoreApplicable(long value) { this.nutriscoreApplicable = value; }

        public String getGrade() { return grade; }
        public void setGrade(String value) { this.grade = value; }

        public long getCategoryAvailable() { return categoryAvailable; }
        public void setCategoryAvailable(long value) { this.categoryAvailable = value; }

        public long getNutriscoreComputed() { return nutriscoreComputed; }
        public void setNutriscoreComputed(long value) { this.nutriscoreComputed = value; }

        public long getNutrientsAvailable() { return nutrientsAvailable; }
        public void setNutrientsAvailable(long value) { this.nutrientsAvailable = value; }
    }


    static class Data {
        private Long fiber;
        private Long isFat;
        private long isBeverage;
        private long isCheese;
        private long isWater;
        private Long fruitsVegetablesNutsColzaWalnutOliveOils;
        private Long isRedMeatProduct;
        private Long isFatOilNutsSeeds;

        public Long getFiber() { return fiber; }
        public void setFiber(Long value) { this.fiber = value; }

        public Long getIsFat() { return isFat; }
        public void setIsFat(Long value) { this.isFat = value; }

        public long getIsBeverage() { return isBeverage; }
        public void setIsBeverage(long value) { this.isBeverage = value; }

        public long getIsCheese() { return isCheese; }
        public void setIsCheese(long value) { this.isCheese = value; }

        public long getIsWater() { return isWater; }
        public void setIsWater(long value) { this.isWater = value; }

        public Long getFruitsVegetablesNutsColzaWalnutOliveOils() { return fruitsVegetablesNutsColzaWalnutOliveOils; }
        public void setFruitsVegetablesNutsColzaWalnutOliveOils(Long value) { this.fruitsVegetablesNutsColzaWalnutOliveOils = value; }

        public Long getIsRedMeatProduct() { return isRedMeatProduct; }
        public void setIsRedMeatProduct(Long value) { this.isRedMeatProduct = value; }

        public Long getIsFatOilNutsSeeds() { return isFatOilNutsSeeds; }
        public void setIsFatOilNutsSeeds(Long value) { this.isFatOilNutsSeeds = value; }
    }

// SelectedImages.java

// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation

    static  class SelectedImages {
        private Front front;

        public Front getFront() { return front; }
        public void setFront(Front value) { this.front = value; }
    }

// Front.java

// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation

    static   class Front {
        private Display small;
        private Display thumb;
        private Display display;

        public Display getSmall() { return small; }
        public void setSmall(Display value) { this.small = value; }

        public Display getThumb() { return thumb; }
        public void setThumb(Display value) { this.thumb = value; }

        public Display getDisplay() { return display; }
        public void setDisplay(Display value) { this.display = value; }
    }

// Display.java

// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation

    static   class Display {
        private String en;

        public String getEn() { return en; }
        public void setEn(String value) { this.en = value; }
    }

}



// CategoriesProperties.java

// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation


// EcoscoreData.java

// YApi QuickType插件生成，具体参考文档:https://plugins.jetbrains.com/plugin/18847-yapi-quicktype/documentation
