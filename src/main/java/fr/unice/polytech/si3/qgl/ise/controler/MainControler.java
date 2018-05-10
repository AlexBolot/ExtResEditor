package fr.unice.polytech.si3.qgl.ise.controler;

import fr.unice.polytech.si3.qgl.ise.model.Biome;
import fr.unice.polytech.si3.qgl.ise.model.CraftedResource;
import fr.unice.polytech.si3.qgl.ise.service.ArrayList8;
import fr.unice.polytech.si3.qgl.ise.service.JsonAccessor;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Popup;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import static fr.unice.polytech.si3.qgl.ise.model.ExtResParser.ExtResBundle;

/*................................................................................................................................
 . Copyright (c)
 .
 . The MainControler class was Coded by : Alexandre BOLOT
 .
 . Last Modified : 10/05/18 17:11
 .
 . Contact : bolotalex06@gmail.com
 ...............................................................................................................................*/

@SuppressWarnings("unchecked")
public class MainControler {

    public AnchorPane anchorPaneCrafted;
    public AnchorPane anchorPaneBiomes;
    public Button btnAddToCrafted;
    public Button btnAddToBiome;
    public Button btnRemoveFromCrafted;
    public Button btnRemoveFromBiome;
    public TextField txtRawResName;
    public TextField txtCraftedName;
    public TextField txtBiomeName;
    public ListView<String> listViewRawRes;
    public TreeView<String> treeViewCrafted;
    public TreeView<String> treeViewBiome;
    public TabPane tabs;

    private ExtResBundle bundle = new ExtResBundle();
    private File storageFile;

    @FXML
    public void initialize() {
        addToCrafted();
        addToBiome();

        selectFile();

        treeViewCrafted.setCellFactory(cell -> new CraftedTreeCell());
        treeViewBiome.setCellFactory(cell -> new BiomeTreeCell());

        listViewRawRes.setCellFactory(listView -> {

            ListCell<String> cell = new ListCell<>();

            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(event -> {
                listView.getItems().remove(cell.getItem());
                commitChanges();
            });

            cell.setContextMenu(new ContextMenu(deleteItem));
            cell.textProperty().bind(cell.itemProperty());

            return cell;
        });
    }

    public void selectFile() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        storageFile = fileChooser.showOpenDialog(new Popup());

        bundle = JsonAccessor.loadBundle(storageFile);

        listViewRawRes.setItems(bundle.getRawResources());

        getNodes(Pane.class, anchorPaneCrafted)
                .forEach(pane -> getNodes(ComboBox.class, pane)
                        .forEach(comboBox -> comboBox.setItems(bundle.getRawResources())));

        getNodes(ComboBox.class, anchorPaneBiomes)
                .forEach(comboBox -> comboBox.setItems(bundle.getRawResources()));

        initCraftedTreeView();
        initBiomesTreeView();
    }

    //--------------------------------------------------//

    public void addRawRes() {
        String rawResName = txtRawResName.getText().trim();

        if (!rawResName.isEmpty() && !bundle.hasRawRes(rawResName)) {
            bundle.addRawRes(rawResName);
            commitChanges();
        }
    }

    public void addCrafted() {
        String craftedName = txtCraftedName.getText().trim();

        if (craftedName.isEmpty()) return;
        if (bundle.hasCraftedRes(craftedName)) return;

        HashMap<String, String> recipe = new HashMap<>();

        getNodes(Pane.class, anchorPaneCrafted).forEach(pane -> {
            ComboBox<String> comboBox = (ComboBox<String>) pane.getChildren().get(0);
            TextField textField = (TextField) pane.getChildren().get(1);

            String item = comboBox.getSelectionModel().getSelectedItem();
            String text = textField.getText().trim();

            if (item != null && !text.isEmpty()) recipe.put(item, text);
        });

        CraftedResource crafted = new CraftedResource(craftedName, recipe);

        bundle.addCraftedRes(crafted);
        updateCraftedTreeView(crafted);
        commitChanges();
    }

    public void addBiome() {
        String biomeName = txtBiomeName.getText().trim();

        ArrayList8<String> rawResources = getNodes(ComboBox.class, anchorPaneBiomes)
                .subList(comboBox -> !comboBox.getSelectionModel().isEmpty())
                .mapAndCollect(comboBox -> comboBox.getSelectionModel().getSelectedItem())
                .mapAndCollect(Object::toString);

        if (biomeName.isEmpty()) return;
        if (bundle.hasBiome(biomeName)) return;

        Biome biome = new Biome(biomeName, rawResources);

        bundle.addBiome(biome);
        updateBiomesTreeView(biome);
        commitChanges();
    }

    //--------------------------------------------------//

    public void addToCrafted() {
        int paneCount = getNodes(Pane.class, anchorPaneCrafted).size();
        int offset = paneCount * 45 + 15;

        Pane pane = createCraftedPane(offset);

        anchorPaneCrafted.getChildren().add(pane);

        btnAddToCrafted.setLayoutY(btnAddToCrafted.getLayoutY() + 45);
        btnRemoveFromCrafted.setLayoutY(btnRemoveFromCrafted.getLayoutY() + 45);
    }

    public void removeFromCrafted() {
        int paneCount = getNodes(Pane.class, anchorPaneCrafted).size();

        if (paneCount == 0) return;

        int offset = (paneCount - 1) * 45 + 15;

        removeLineFromAnchorPane(anchorPaneCrafted, btnAddToCrafted, btnRemoveFromCrafted, offset);
    }

    public void addToBiome() {
        int comboBoxesCount = getNodes(ComboBox.class, anchorPaneBiomes).size();
        int offset = comboBoxesCount * 45 + 15;

        ComboBox<String> ddl = new ComboBox<>(bundle.getRawResources());
        ddl.setPromptText("Resource");
        ddl.setPrefWidth(245.5);
        ddl.setPrefHeight(30);
        ddl.setLayoutX(15);
        ddl.setLayoutY(offset);

        anchorPaneBiomes.getChildren().add(ddl);

        btnAddToBiome.setLayoutY(btnAddToBiome.getLayoutY() + 45);
        btnRemoveFromBiome.setLayoutY(btnRemoveFromBiome.getLayoutY() + 45);
    }

    public void removeFromBiome() {
        int comboBoxesCount = getNodes(ComboBox.class, anchorPaneBiomes).size();

        if (comboBoxesCount == 0) return;

        int offset = (comboBoxesCount - 1) * 45 + 15;

        removeLineFromAnchorPane(anchorPaneBiomes, btnAddToBiome, btnRemoveFromBiome, offset);
    }

    //--------------------------------------------------//

    private void removeLineFromAnchorPane(AnchorPane anchorPane, Button btnAdd, Button btnDelete, int offset) {
        anchorPane.getChildren().removeIf(node -> node.getLayoutY() == offset);

        btnAdd.setLayoutY(btnAdd.getLayoutY() - 45);
        btnDelete.setLayoutY(btnDelete.getLayoutY() - 45);
    }

    private void initCraftedTreeView() {
        DecimalFormat df = new DecimalFormat("##.##");
        df.setRoundingMode(RoundingMode.DOWN);

        Platform.runLater(() -> {
            TreeItem<String> root = new TreeItem<>("crafted-resources");
            root.setExpanded(true);

            for (CraftedResource craftedResource : bundle.getCraftedResources()) {
                TreeItem<String> craftedItem = new TreeItem<>(craftedResource.getName());

                for (Map.Entry<String, String> entry : craftedResource.getRecipe().entrySet()) {
                    craftedItem.getChildren().add(new TreeItem<>(entry.getKey() + " : " + entry.getValue()));
                }

                root.getChildren().add(craftedItem);
            }

            treeViewCrafted.setRoot(root);
            treeViewCrafted.setShowRoot(false);
            treeViewCrafted.refresh();
        });
    }

    private void initBiomesTreeView() {
        Platform.runLater(() -> {
            TreeItem<String> root = new TreeItem<>("biomes");
            root.setExpanded(true);

            for (Biome biome : bundle.getBiomes()) {

                TreeItem<String> biomeItem = new TreeItem<>(biome.getName());

                for (String rawRes : biome.getResources()) {
                    biomeItem.getChildren().add(new TreeItem<>(rawRes));
                }

                root.getChildren().add(biomeItem);
            }

            treeViewBiome.setRoot(root);
            treeViewBiome.setShowRoot(false);
            treeViewBiome.refresh();
        });
    }

    private void updateCraftedTreeView(CraftedResource crafted) {
        TreeItem<String> craftedItem = new TreeItem<>(crafted.getName());

        for (Map.Entry<String, String> entry : crafted.getRecipe().entrySet()) {
            craftedItem.getChildren().add(new TreeItem<>(entry.getKey() + " : " + entry.getValue()));
        }

        treeViewCrafted.getRoot().getChildren().add(craftedItem);
    }

    private void updateBiomesTreeView(Biome biome) {
        TreeItem<String> biomeItem = new TreeItem<>(biome.getName());

        for (String rawRes : biome.getResources()) {
            biomeItem.getChildren().add(new TreeItem<>(rawRes));
        }

        treeViewBiome.getRoot().getChildren().add(biomeItem);
    }

    private <T> ArrayList8<T> getNodes(Class<T> tClass, Pane pane) {
        return new ArrayList8<>(pane.getChildren())
                .subList(tClass::isInstance)
                .mapAndCollect(tClass::cast);
    }

    private Pane createCraftedPane(int offset) {
        ComboBox<String> ddl = new ComboBox<>(bundle.getRawResources());
        ddl.setPromptText("Ingrédient");
        ddl.setPrefWidth(160);
        ddl.setPrefHeight(30);
        ddl.setLayoutX(0);
        ddl.setLayoutY(0);

        TextField txt = new TextField();
        txt.setPromptText("Quantité");
        txt.setPrefWidth(70);
        txt.setPrefHeight(30);
        txt.setLayoutX(175);
        txt.setLayoutY(0);

        Pane pane = new Pane();
        pane.setPrefWidth(245.5);
        pane.setPrefHeight(30);
        pane.setLayoutX(15);
        pane.setLayoutY(offset);

        pane.getChildren().addAll(ddl, txt);

        return pane;
    }

    private void commitChanges() {
        JsonAccessor.writeBunlde(storageFile, bundle);
    }

    private final class BiomeTreeCell extends TreeCell<String> {

        BiomeTreeCell() {
            MenuItem delete = new MenuItem("Delete");
            delete.setOnAction((EventHandler) t -> {

                TreeItem<String> item = getTreeItem();
                TreeItem<String> parent = item.getParent();

                if (item.isLeaf()) {
                    bundle.getBiome(parent.getValue()).getResources().remove(item.getValue());
                } else {
                    bundle.getBiomes().remove(bundle.getBiome(item.getValue()));
                }

                parent.getChildren().remove(item);
                commitChanges();
            });
            setContextMenu(new ContextMenu(delete));
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setText(empty ? null : getItem());
            setGraphic(empty ? null : getTreeItem().getGraphic());
        }
    }

    private final class CraftedTreeCell extends TreeCell<String> {

        CraftedTreeCell() {
            MenuItem delete = new MenuItem("Delete");
            delete.setOnAction((EventHandler) t -> {

                TreeItem<String> item = getTreeItem();
                TreeItem<String> parent = item.getParent();

                String valueName = item.getValue().split(":")[0].trim();

                if (item.isLeaf()) {
                    bundle.getCraftedRes(parent.getValue()).getRecipe().remove(valueName);
                } else {
                    bundle.getCraftedResources().remove(bundle.getCraftedRes(valueName));
                }

                parent.getChildren().remove(item);
                commitChanges();
            });
            setContextMenu(new ContextMenu(delete));
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setText(empty ? null : getItem());
            setGraphic(empty ? null : getTreeItem().getGraphic());
        }
    }
}