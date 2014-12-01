package scripts.MassFighter.Data;

/**
 * Created by Ozzy on 09/11/2014.
 */
public enum Potion {

    PRAYER_FLASK("PRAYER_FLASK", 23243,23245,23247,23249,23251,23253),
    PRAYER_POTION("PRAYER_POTION", 2434,139,141,143);


    private String name;
    private int[] ids;
    private Potion(String name, int... ids) {
        this.name = name;
        this.ids = ids;
    }
    public String getName() {
        return name;
    }
    public int[] getIds() {
        return ids;
    }

}
