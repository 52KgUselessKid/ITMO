import Pokemons.*;
import ru.ifmo.se.pokemon.*;

public class Main
{
    public static void main(String[] args)
    {
        Battle b = new Battle();

        Kangaskhan kangaskhan = new Kangaskhan("Kangas", 1);
        Barboach barboach = new Barboach("Barbo", 1);
        Whiscash whiscash = new Whiscash("Whis", 1);

        Porygon porygon = new Porygon("pGon", 1);
        Porygon2 porygon2 = new Porygon2("pGon2", 1);
        PorygonZ porygonZ = new PorygonZ("pGonZ", 1);

        b.addAlly(kangaskhan);
        b.addAlly(barboach);
        b.addAlly(whiscash);

        b.addFoe(porygon);
        b.addFoe(porygon2);
        b.addFoe(porygonZ);

        b.go();
    }
}