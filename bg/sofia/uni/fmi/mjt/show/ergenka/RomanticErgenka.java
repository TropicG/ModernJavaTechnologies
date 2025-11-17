package bg.sofia.uni.fmi.mjt.show.ergenka;

import bg.sofia.uni.fmi.mjt.show.date.DateEvent;

public class RomanticErgenka implements Ergenka {

    private final String name;
    private final String favDateLoc;
    private final int romanceLevel;
    private final int humorLevel;
    private int rating;
    private final short age;

    public RomanticErgenka(String name, short age, int romanticLevel, int humorLevel, int rating, String favDateLoc) {
        this.name = (name == null) ? "Ergenka" : name; // all should have names
        this.age = (age < 18) ? 18 : age; // minimum age for participating
        this.romanceLevel = romanticLevel;
        this.humorLevel = humorLevel;
        this.rating = rating; // the rating cannot be below zero
        this.favDateLoc = (favDateLoc == null || favDateLoc.isBlank()) ? "dinner with wine" : favDateLoc;
        // since this ergenka will be romantic the default best date for here is dinner with wine
    }

    public RomanticErgenka(RomanticErgenka otherErgenka){
        this.name = otherErgenka.getName();
        this.romanceLevel = otherErgenka.getRomanceLevel();
        this.humorLevel = otherErgenka.getHumorLevel();
        this.rating = otherErgenka.getRating();
        this.age = otherErgenka.getAge();
        this.favDateLoc = otherErgenka.favDateLoc;
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
    public int getHumorLevel() {
        return humorLevel;
    }

    @Override
    public int getRating() {
        return rating;
    }

    @Override
    public void reactToDate(DateEvent dateEvent) {
        rating += ( (romanceLevel * 7) / dateEvent.getTensionLevel());
        rating += (int) Math.floor((double) humorLevel / 3);
        rating += calculateBonus(dateEvent);
    }

    private int calculateBonus(DateEvent dateEvent) {
        int bonus = 0;

        bonus += dateEvent.getLocation().equalsIgnoreCase(favDateLoc) ? 5 : 0;
        bonus -= dateEvent.getDuration() < 30 ? 3 : 0;
        bonus -= dateEvent.getDuration() > 120 ? 2 : 0;

        return bonus;
    }
}
