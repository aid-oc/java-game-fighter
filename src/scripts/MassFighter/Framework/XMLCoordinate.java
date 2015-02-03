package scripts.MassFighter.Framework;

import com.runemate.game.api.hybrid.location.Coordinate;

public class XMLCoordinate {

    public int x;
    public int y;
    public int z;

    public XMLCoordinate() {
        // required by Jaxb to unmarshal
    }

    public XMLCoordinate(Coordinate coordinate) {
        this.x = coordinate.getX();
        this.y = coordinate.getY();
        this.z = coordinate.getPlane();
    }


}
