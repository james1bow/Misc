package misc.mounts;

import net.risingworld.api.Server;
import net.risingworld.api.events.player.ui.PlayerUIElementClickEvent;
import net.risingworld.api.objects.Player;
import net.risingworld.api.ui.UIElement;
import net.risingworld.api.ui.UILabel;
import net.risingworld.api.ui.UIScrollView;
import net.risingworld.api.ui.style.Pivot;
import net.risingworld.api.ui.style.TextAnchor;

import java.util.HashMap;
import java.util.Map;

public class MountList {

    private Player player;
    private HashMap<Long, MountController> controllers;
    private UIScrollView mountScroll;
    private UILabel mountCountLabel;

    public MountList(Player player) {
        this.player = player;
        controllers = MountProtect.getAllMountControllers();
        buildList();
    }

    private void buildList(){
        UIElement panel = new UIElement();
        panel.setPosition(50.0f, 50.0f, true);
        panel.setSize(30.0f, 85.0f, true);
        panel.setBackgroundColor(0,0,0,1);
        panel.setBorder(1.5f);
        panel.setBorderColor(1,1,1,1);
        panel.setPivot(Pivot.MiddleCenter);
        panel.setVisible(true);
        panel.setBorderEdgeRadius(10, false);

        mountCountLabel = new UILabel("Total Mounts: "+controllers.size());
        mountCountLabel.setFontSize(18);
        mountCountLabel.setPivot(Pivot.MiddleCenter);
        mountCountLabel.setPosition(50.0f, 3.0f, true);
        mountCountLabel.setVisible(true);

        mountScroll = new UIScrollView(UIScrollView.ScrollViewMode.Vertical);
        mountScroll.setPivot(Pivot.MiddleCenter);
        mountScroll.setSize(90.0f,85.0f, true);
        mountScroll.setPosition(50.0f,50.0f,true);
        mountScroll.setBorder(1.5f);
        mountScroll.setBorderColor(1,1,1,1);
        mountScroll.setBorderEdgeRadius(10, false);
        mountScroll.setVisible(true);
        mountScroll.style.paddingTop.set(4);
        mountScroll.style.paddingBottom.set(4);

        //refresh button
        UILabel refreshButton = new UILabel("Refresh"){
            @Override
            public void onClick(PlayerUIElementClickEvent event) {
               refresh();
            }
        };
       refreshButton.setBorderEdgeRadius(10, false);
       refreshButton.setPivot(Pivot.MiddleCenter);
       refreshButton.setClickable(true);
       refreshButton.setVisible(true);
       refreshButton.setSize(25f, 3, true);
       refreshButton.setPosition(75f, 97f, true);
       refreshButton.setBorder(1.5f);
       refreshButton.setBorderColor(1,1,1,1);
       refreshButton.setBorderEdgeRadius(10, false);

        //close button
        UILabel closeButton = new UILabel("Close"){
            @Override
            public void onClick(PlayerUIElementClickEvent event) {
                player.removeUIElement(panel);
                player.setMouseCursorVisible(false);
            }
        };
        closeButton.setBorderEdgeRadius(10, false);
        closeButton.setPivot(Pivot.MiddleCenter);
        closeButton.setClickable(true);
        closeButton.setVisible(true);
        closeButton.setSize(25f, 3, true);
        closeButton.setPosition(25f, 97f, true);
        closeButton.setBorder(1.5f);
        closeButton.setBorderColor(1,1,1,1);
        closeButton.setBorderEdgeRadius(10, false);


        panel.addChild(mountCountLabel);
        panel.addChild(mountScroll);
        panel.addChild(closeButton);
        panel.addChild(refreshButton);
        player.addUIElement(panel);
        buildElements();
        player.setMouseCursorVisible(true);
    }

    private void buildElements(){
        for (Map.Entry<Long, MountController> entry : controllers.entrySet()) {
            UIElement listElement = new UIElement();
            listElement.setBorderEdgeRadius(10, false);
            listElement.setBorder(1.5f);
            listElement.setBorderColor(1,1,1,1);
            listElement.style.height.set(125);
            listElement.setVisible(true);
            listElement.style.marginTop.set(4);
            listElement.style.marginLeft.set(10);
            listElement.style.marginRight.set(10);

            UILabel idLabel = new UILabel("ID: "+entry.getValue().getID());
            idLabel.setPosition(5, 22, true);
            idLabel.setPivot(Pivot.MiddleLeft);
            idLabel.setTextAlign(TextAnchor.MiddleLeft);
            idLabel.setFontSize(14);
            idLabel.setVisible(true);

            UILabel nameLabel = new UILabel("Name: "+entry.getValue().getName());
            nameLabel.setPosition(5, 44, true);
            nameLabel.setPivot(Pivot.MiddleLeft);
            nameLabel.setTextAlign(TextAnchor.MiddleLeft);
            nameLabel.setFontSize(14);
            nameLabel.setVisible(true);

            UILabel ownerLabel = new UILabel("Owner: "+Server.getLastKnownPlayerName(entry.getValue().getOwnerID()));
            ownerLabel.setPosition(5, 66, true);
            ownerLabel.setPivot(Pivot.MiddleLeft);
            ownerLabel.setTextAlign(TextAnchor.MiddleLeft);
            ownerLabel.setFontSize(14);
            ownerLabel.setVisible(true);

            UILabel ownerIDLabel = new UILabel("Owner ID: "+entry.getValue().getOwnerID());
            ownerIDLabel.setPosition(5, 88, true);
            ownerIDLabel.setPivot(Pivot.MiddleLeft);
            ownerIDLabel.setTextAlign(TextAnchor.MiddleLeft);
            ownerIDLabel.setFontSize(14);
            ownerIDLabel.setVisible(true);

            UILabel gotoLabel = new UILabel("GOTO"){
                @Override
                public void onClick(PlayerUIElementClickEvent event) {
                    Player player = event.getPlayer();
                    player.setPosition(entry.getValue().getNpc().getPosition());
                }
            };
            gotoLabel.setPosition(65, 23, true);
            gotoLabel.setSize(12, 20, true);
            gotoLabel.setPivot(Pivot.MiddleLeft);
            gotoLabel.setTextAlign(TextAnchor.MiddleCenter);
            gotoLabel.setFontSize(14);
            gotoLabel.setVisible(true);
            gotoLabel.setClickable(true);
            gotoLabel.setBorder(1);
            gotoLabel.setBorderColor(1,1,1,1);
            gotoLabel.setBorderEdgeRadius(10, false);

            UILabel bringLabel = new UILabel("Bring"){
                @Override
                public void onClick(PlayerUIElementClickEvent event) {
                    entry.getValue().getNpc().setPosition(player.getPosition());
                }
            };
            bringLabel.setPosition(65, 50, true);
            bringLabel.setSize(12, 20, true);
            bringLabel.setPivot(Pivot.MiddleLeft);
            bringLabel.setTextAlign(TextAnchor.MiddleCenter);
            bringLabel.setFontSize(14);
            bringLabel.setVisible(true);
            bringLabel.setClickable(true);
            bringLabel.setBorder(1);
            bringLabel.setBorderColor(1,1,1,1);
            bringLabel.setBorderEdgeRadius(10, false);

            UILabel renameLabel = new UILabel("Rename"){
                @Override
                public void onClick(PlayerUIElementClickEvent event) {
                    Player player = event.getPlayer();
                    entry.getValue().rename(player);
                }
            };
            renameLabel.setPosition(65, 77, true);
            renameLabel.setSize(12, 20, true);
            renameLabel.setPivot(Pivot.MiddleLeft);
            renameLabel.setTextAlign(TextAnchor.MiddleCenter);
            renameLabel.setFontSize(14);
            renameLabel.setVisible(true);
            renameLabel.setClickable(true);
            renameLabel.setBorder(1);
            renameLabel.setBorderColor(1,1,1,1);
            renameLabel.setBorderEdgeRadius(10, false);

            listElement.addChild(idLabel);
            listElement.addChild(nameLabel);
            listElement.addChild(ownerLabel);
            listElement.addChild(ownerIDLabel);
            listElement.addChild(gotoLabel);
            listElement.addChild(bringLabel);
            listElement.addChild(renameLabel);
            mountScroll.addChild(listElement);
        }
    }

    public void refresh(){
        mountScroll.removeAllChilds();
        mountScroll.updateStyle();
        controllers = MountProtect.getAllMountControllers();
        mountCountLabel.setText("Total Mounts: "+controllers.size());
        buildElements();
    }

}
