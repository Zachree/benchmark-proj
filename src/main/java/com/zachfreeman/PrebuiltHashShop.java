package com.zachfreeman;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class PrebuiltHashShop {
    final int STOCK = 100, ARROW_STOCK = 1000;
    ConcurrentHashMap<String, Integer> chmShop = new ConcurrentHashMap<>();

    private final class Character {
        final String charClass = generateClass();
        final String[] wishlist = generateWishlist();   // class-specific item wishlist
        Inventory inventory = new Inventory(charClass);
        Item observedItem;

        private String generateClass() {
            String[] classes = {"Knight", "Archer", "Healer", "Mage"};
            int rand = ThreadLocalRandom.current().nextInt(classes.length);
            return classes[rand];
        }
        private String[] generateWishlist() {
            if(charClass.equals("Knight"))
                return new String[]{"sword", "shield", "heavy armor", "health potion"};
            if(charClass.equals("Archer"))
                return new String[]{"bow", "arrow", "medium armor", "health potion"};
            if(charClass.equals("Mage"))
                return new String[]{"spell scroll", "light armor", "mana potion", "health potion"};
            if(charClass.equals("Healer"))
                return new String[]{"healing herbs", "light armor", "mana potion", "health potion"};
            return new String[]{};
        }
    }

    private final class Inventory {
        Item[] items = new Item[4];    // max inventory size of 4

        public Inventory(String charClass) {
            if(charClass.equals("Knight")){
                items[0] = new Item("sword", 1);
                items[1] = new Item("shield", 1);
                items[2] = new Item("heavy armor", 1);
                items[3] = new Item("health potion", 1);
            }
            if(charClass.equals("Archer")){
                items[0] = new Item("bow", 1);
                items[1] = new Item("arrow", 10);
                items[2] = new Item("medium armor", 1);
                items[3] = new Item("health potion", 1);
            }
            if(charClass.equals("Mage")){
                items[0] = new Item("spell scroll", 1);
                items[1] = new Item("light armor", 1);
                items[2] = new Item("health potion", 1);
                items[3] = new Item("mana potion", 1);
            }
            if(charClass.equals("Healer")){
                items[0] = new Item("healing herbs", 1);
                items[1] = new Item("light armor", 1);
                items[2] = new Item("health potion", 1);
                items[3] = new Item("mana potion", 1);
            }
        }
        // find index of requested item, returning an out of bounds number if it doesn't exist
        public int find(String item) {
            for(int i = 0; i < items.length; i++) {
                if(items[i].name.equalsIgnoreCase(item))
                    return i;
            }
            return 10000;
        }
    }

    private final class Item {
        String name;
        int amount;
        public Item(String name, int amount){
            this.name = name;
            this.amount = amount;
        }
    }

    public void simulateShopper(double writerRatio) {
        Character c = new Character();
        populateShop();

        double rand = ThreadLocalRandom.current().nextDouble();
        if(rand < writerRatio) {
            chmBuyer(c);
        } else {
            chmWindowshopper(c);
        }
    }

    private void chmWindowshopper(Character c) {
        // select random thing from wishlist
        int rand = ThreadLocalRandom.current().nextInt(c.wishlist.length);
        String chosenItem = c.wishlist[rand];
        // see if it's in stock
        int stock = chmShop.get(chosenItem);
        if (stock > 0) {
            c.observedItem = new Item(chosenItem, stock);
        }
    }

    private void chmBuyer(Character c) {
        double rand = ThreadLocalRandom.current().nextDouble();
        if(rand < 0.8)
            chmBuy(c);
        else
            chmSell(c);
    }

    private void chmBuy(Character c) {
        // select random thing from wishlist
        int rand = ThreadLocalRandom.current().nextInt(c.wishlist.length);
        String chosenItem = c.wishlist[rand];
        int x = c.inventory.find(chosenItem);
        int stock = chmShop.get(chosenItem);
        // see if it's in stock, if so, buy it
        if (stock > 0) {
            if (!chosenItem.equals("arrow")) {
                chmShop.put(chosenItem, stock - 1);
                c.inventory.items[x].amount++;
            } else {                                // buy arrows in increments of 10
                chmShop.put(chosenItem, stock - 10);
                c.inventory.items[x].amount += 10;
            }
        }
        // else sell THAT item to the shop
        else {
            if (!chosenItem.equals("arrow")) {
                chmShop.put(chosenItem, stock + 1);
                c.inventory.items[x].amount--;
            } else {                                // buy arrows in increments of 10
                chmShop.put(chosenItem, stock + 10);
                c.inventory.items[x].amount -= 10;
            }
        }
    }

    private void chmSell(Character c) {
        // select random thing from inventory
        int rand = ThreadLocalRandom.current().nextInt(c.wishlist.length);
        String chosenItem = c.wishlist[rand];
        int stock = chmShop.get(chosenItem);
        // sell it
        int x = c.inventory.find(chosenItem);
        if (!chosenItem.equals("arrow")) {
            chmShop.put(chosenItem, stock + 1);
            c.inventory.items[x].amount--;
        } else {                                // buy arrows in increments of 10
            chmShop.put(chosenItem, stock + 10);
            c.inventory.items[x].amount -= 10;
        }
    }

    private void populateShop() {
        chmShop.put("sword", STOCK);
        chmShop.put("shield", STOCK);
        chmShop.put("light armor", STOCK);
        chmShop.put("medium armor", STOCK);
        chmShop.put("heavy armor", STOCK);
        chmShop.put("bow", STOCK);
        chmShop.put("arrow", ARROW_STOCK);
        chmShop.put("health potion", STOCK);
        chmShop.put("mana potion", STOCK);
        chmShop.put("spell scroll", STOCK);
        chmShop.put("healing herbs", STOCK);
    }
}
