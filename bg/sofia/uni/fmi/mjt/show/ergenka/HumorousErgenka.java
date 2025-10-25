package bg.sofia.uni.fmi.mjt.show.ergenka;

import bg.sofia.uni.fmi.mjt.show.date.DateEvent;

public class HumorousErgenka implements Ergenka {

    private String name;
    private int romanceLevel;
    private int humorLevel;
    private int rating;
    private short age;

    public HumorousErgenka(String name, short age, int romanticLevel, int humorLevel, int rating) {
        this.name = name;
        this.age = age;
        this.romanceLevel = romanticLevel;
        this.humorLevel = humorLevel;
        this.rating = rating;
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
        int bonus = calculateBonus(dateEvent);

        rating += (int)((humorLevel * 5) / dateEvent.getTensionLevel());
        rating += (int)(romanceLevel / 3);
        rating += bonus;
    }

    private int calculateBonus(DateEvent dateEvent) {
        int bonus = 0;

        bonus += dateEvent.getDuration() >= 30 & dateEvent.getDuration() <= 90 ? 4 : 0;
        bonus -= dateEvent.getDuration() < 30 ? -2 : 0;
        bonus -= dateEvent.getDuration() > 90 ? -3 : 0;

        return bonus;
    }
}
