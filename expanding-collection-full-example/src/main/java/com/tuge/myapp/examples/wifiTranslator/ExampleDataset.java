package com.tuge.myapp.examples.wifiTranslator;

import com.tuge.myapp.ECCardData;
import com.tuge.myapp.examples.wifiTranslator.pojo.CardData;
import com.tuge.myapp.examples.wifiTranslator.pojo.Comment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ExampleDataset {

    private List<ECCardData> dataset;

    public ExampleDataset() {
        dataset = new ArrayList<>(6);

        CardData item1 = new CardData();
        item1.setMainBackgroundResource(R.drawable.card01);
        item1.setHeadBackgroundResource(R.drawable.card01_head);
        item1.setHeadTitle("Translation");
        item1.setPersonMessage("Dialogue Translation");
        item1.setPersonName("翻一翻");
        item1.setPersonPictureResource(R.drawable.icon1);
        item1.setListItems(prepareCommentsArray());
        dataset.add(item1);

        CardData item2 = new CardData();
        item2.setMainBackgroundResource(R.drawable.card02);
        item2.setHeadBackgroundResource(R.drawable.card02_head);
        item2.setHeadTitle("Photo Translation");
        item2.setPersonName("拍一拍");
        item2.setPersonMessage("Photo Translation");
        item2.setListItems(prepareCommentsArray());
        item2.setPersonPictureResource(R.drawable.icon2);
        dataset.add(item2);

        CardData item3 = new CardData();
        item3.setMainBackgroundResource(R.drawable.card03);
        item3.setHeadBackgroundResource(R.drawable.card03_head);
        item3.setHeadTitle("Intelligent Q&A");
        item3.setPersonMessage("Intelligent Q&A");
        item3.setPersonName("问一问");
        item3.setPersonPictureResource(R.drawable.icon3);
        item3.setListItems(prepareCommentsArray());
        dataset.add(item3);

        CardData item4 = new CardData();
        item4.setMainBackgroundResource(R.drawable.card04);
        item4.setHeadBackgroundResource(R.drawable.card04_head);
        item4.setHeadTitle("Simultaneous");
        item4.setPersonMessage("Simultaneous interpretation");
        item4.setPersonName("同声传译");
        item4.setPersonPictureResource(R.drawable.icon4);
        item4.setListItems(prepareCommentsArray());
        dataset.add(item4);

        CardData item5 = new CardData();
        item5.setMainBackgroundResource(R.drawable.card05);
        item5.setHeadBackgroundResource(R.drawable.card05_head);
        item5.setHeadTitle("Group Translation");
        item5.setPersonMessage("Group Translation");
        item5.setPersonName("组群翻译");
        item5.setPersonPictureResource(R.drawable.icon5);
        item5.setListItems(prepareCommentsArray());
        dataset.add(item5);

        CardData item6 = new CardData();
        item6.setMainBackgroundResource(R.drawable.card06);
        item6.setHeadBackgroundResource(R.drawable.card06_head);
        item6.setHeadTitle("System Settings");
        item6.setPersonMessage("System Settings");
        item6.setPersonName("系统设置");
        item6.setPersonPictureResource(R.drawable.icon6);
        item6.setListItems(prepareCommentsArray());
        dataset.add(item6);

    }

    public List<ECCardData> getDataset() {
//        Collections.shuffle(dataset);
        return dataset;
    }

    private List<Comment> prepareCommentsArray() {
        Random random = new Random();
        List<Comment> comments = new ArrayList<>();
        comments.addAll(Arrays.asList(
                new Comment(R.drawable.aaron_bradley, "Aaron Bradley", "When the sensor experiments for deep space, all mermaids accelerate mysterious, vital moons.", "jan 12, 2014"),
                new Comment(R.drawable.barry_allen, "Barry Allen", "It is a cold powerdrain, sir.", "jun 1, 2015"),
                new Comment(R.drawable.bella_holmes, "Bella Holmes", "Particle of a calm shield, control the alignment!", "sep 21, 1937"),
                new Comment(R.drawable.caroline_shaw, "Caroline Shaw", "The human kahless quickly promises the phenomenan.", "may 23, 1967"),
                new Comment(R.drawable.connor_graham, "Connor Graham", "Ionic cannon at the infinity room was the sensor of voyage, imitated to a dead pathway.", "sep 1, 1972"),
                new Comment(R.drawable.deann_hunt, "Deann Hunt", "Vital particles, to the port.", "aug 13, 1995"),
                new Comment(R.drawable.ella_cole, "Ella Cole", "Stars fly with hypnosis at the boldly infinity room!", "nov 18, 1952"),
                new Comment(R.drawable.jayden_shaw, "Jayden Shaw", "Hypnosis, definition, and powerdrain.", "apr 1, 2013"),
                new Comment(R.drawable.jerry_carrol, "Jerry Carrol", "When the queen experiments for nowhere, all particles control reliable, cold captains.", "nov 14, 1964"),
                new Comment(R.drawable.lena_lucas, "Lena Lukas", "When the c-beam experiments for astral city, all cosmonauts acquire remarkable, virtual lieutenant commanders.", "may 4, 1965"),
                new Comment(R.drawable.leonrd_kim, "Leonard Kim", "Starships walk with love at the cold parallel universe!", "jul 3, 1974"),
                new Comment(R.drawable.marc_baker, "Mark Baker", "Friendship at the bridge that is when quirky green people yell.", "dec 24, 1989")));
        Collections.shuffle(comments);
        return comments.subList(0, 6 + random.nextInt(5));
    }
}
