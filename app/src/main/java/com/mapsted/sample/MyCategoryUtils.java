package com.mapsted.sample;

import com.mapsted.positioning.CoreApi;
import com.mapsted.positioning.core.network.property_metadata.model.Category;
import com.mapsted.ui.MapUiApi;

import java.util.ArrayList;
import java.util.List;

public class MyCategoryUtils {
    public static List<MyCategory> createSomeItems() {

        List<MyCategory> myCategories = new ArrayList<>();
        myCategories.add(new MyCategory("asdf-zxvc-asfa", "Clothings", "https://ui-avatars.com/api/?name=A"));
        myCategories.add(new MyCategory("asdf-zxvc-asfd", "Shoes", "https://ui-avatars.com/api/?name=B"));
        myCategories.add(new MyCategory("zvcx-asdf-hjlk", "Jewelry", "https://ui-avatars.com/api/?name=C"));
        myCategories.add(new MyCategory("asdf-qwwere-asfd", "Electronics", "https://ui-avatars.com/api/?name=D"));
        myCategories.add(new MyCategory("cde-asdf-asfd", "Books", "https://ui-avatars.com/api/?name=E"));
        myCategories.add(new MyCategory("asdf-erw-asfd", "Bags", "https://ui-avatars.com/api/?name=F"));
        myCategories.add(new MyCategory("zvcx-w-hjlk", "Restaurants", "https://ui-avatars.com/api/?name=G"));
        myCategories.add(new MyCategory("asdf-qqwwre-asfd", "Household", "https://ui-avatars.com/api/?name=H"));
        myCategories.add(new MyCategory("asdf-asdf-asfd", "Furniture", "https://ui-avatars.com/api/?name=I"));
        myCategories.add(new MyCategory("asdf-we-asfd", "Sports", "https://ui-avatars.com/api/?name=J"));
        myCategories.add(new MyCategory("we-asdf-hjlk", "Toys", "https://ui-avatars.com/api/?name=K"));
        myCategories.add(new MyCategory("asdf-qwre-we", "Garden", "https://ui-avatars.com/api/?name=L"));
        myCategories.add(new MyCategory("asqwedf-asdf-asfd", "Grocery", "https://ui-avatars.com/api/?name=M"));
        myCategories.add(new MyCategory("asdf-zxvc-xcx", "Pharmacy", "https://ui-avatars.com/api/?name=N"));
        myCategories.add(new MyCategory("zvcx-zx-hjlk", "HelpDesk", "https://ui-avatars.com/api/?name=O"));
        myCategories.add(new MyCategory("asdf-css-qwe", "Restrooms", "https://ui-avatars.com/api/?name=P"));
        return myCategories;

    }

    public static List<Category> createCategoryList(MapUiApi sdk) {
        CoreApi coreApi = sdk.getMapApi().getCoreApi();
        List<Category> myCategories = new ArrayList<>();
        /*CategoryTree categoryTree = coreApi.getGlobalCategoryTree();

        String[] cateIds = new String[]{"c2d2-d24d-12085",
                "c2d2-d24d-12086",
                "c2d2-d24d-12087",
                "c2d2-d24d-12093",
                "c2d2-d24d-12096",
                "c2d2-d24d-12090",
                "c2d2-d24d-12100",
                "c2d2-d24d-12101"};
        for (String catId : cateIds) {
            Category category = categoryTree.getCategory(catId);
            Logger.v("categoryId %s -> %s", catId, category);
            myCategories.add(category);
        }*/
        return myCategories;
    }
}