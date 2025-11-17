package bg.sofia.uni.fmi.mjt.show.ergenka;

import bg.sofia.uni.fmi.mjt.show.date.DateEvent;

public class HumorousErgenka implements Ergenka {

    private final String name;
    private final int romanceLevel;
    private final int humorLevel;
    private int rating;
    private final short age;

    public HumorousErgenka(String name, short age, int romanticLevel, int humorLevel, int rating) {
        this.name = (name == null) ? "" : name; // all should have names
        this.age = (age < 18) ? 18 : age; // minimum age for participating
        this.romanceLevel = romanticLevel;  // the romance level cannot be below zero
        this.humorLevel = humorLevel; // the humour level cannot be below zero
        this.rating = rating; // the rating cannot be below zero
    }

    public HumorousErgenka(HumorousErgenka otherErgenka){
        this.name = otherErgenka.getName();
        this.romanceLevel = otherErgenka.getRomanceLevel();
        this.humorLevel = otherErgenka.getHumorLevel();
        this.rating = otherErgenka.getRating();
        this.age = otherErgenka.getAge();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public short getAge() {
        return age;
    }

    @Override
    public int getRomanceLevel() {
        return romanceLevel;
    }

    @Override
    public int getHumorLevel(){
        return humorLevel;
    }

    @Override
    public int getRating() {
        return rating;
    }


    @Override
    public void reactToDate(DateEvent dateEvent) {
        rating += ((humorLevel * 5) / dateEvent.getTensionLevel());
        rating += (int) Math.floor((double) romanceLevel / (double)3);
        rating += calculateBonus(dateEvent);
    }
    private int calculateBonus(DateEvent dateEvent) {
        int bonus = 0;

        bonus += dateEvent.getDuration() >= 30 && dateEvent.getDuration() <= 90 ? 4 : 0;
        bonus -= dateEvent.getDuration() < 30 ? 2 : 0;
        bonus -= dateEvent.getDuration() > 90 ? 3 : 0;

        return bonus;
    }
}
