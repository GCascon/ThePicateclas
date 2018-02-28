package the.picateclas.pizzaslices.test;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import the.picateclas.pizzaslices.Main;

public class PizzaSlicesTest {

    public PizzaSlicesTest() {
    }

    @Test
    public void exampleTest() throws IOException {
        Main.main(new String[] { "example.in" });
        File output = new File("example.out");
        Assert.assertTrue(output.exists());
    }

    @Test
    public void smallTest() throws IOException {
        Main.main(new String[] { "small.in" });
        File output = new File("small.out");
        Assert.assertTrue(output.exists());
    }

    @Test
    public void mediumTest() throws IOException {
        Main.main(new String[] { "medium.in" });
        File output = new File("medium.out");
        Assert.assertTrue(output.exists());
    }

    @Test
    public void bigTest() throws IOException {
        Main.main(new String[] { "big.in" });
        File output = new File("big.out");
        Assert.assertTrue(output.exists());
    }
}
