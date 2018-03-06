package the.picateclas.planificador.test;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import the.picateclas.planificador.Main;

public class PlanificadorTest {

    public PlanificadorTest() {
    }

    @Test
    public void exampleTest() throws IOException {
        Main.main(new String[] { "a_example.in" });
        File output = new File("a_example.out");
        Assert.assertTrue(output.exists());
    }

    @Test
    public void shouldBeEasy() throws IOException {
        Main.main(new String[] { "b_should_be_easy.in" });
        File output = new File("b_should_be_easy.out");
        Assert.assertTrue(output.exists());
    }

    @Test
    public void noHurry() throws IOException {
        Main.main(new String[] { "c_no_hurry.in" });
        File output = new File("c_no_hurry.out");
        Assert.assertTrue(output.exists());
    }

    @Test
    public void metropolisTest() throws IOException {
        Main.main(new String[] { "d_metropolis.in" });
        File output = new File("d_metropolis.out");
        Assert.assertTrue(output.exists());
    }

    @Test
    public void highBonus() throws IOException {
        Main.main(new String[] { "e_high_bonus.in" });
        File output = new File("e_high_bonus.out");
        Assert.assertTrue(output.exists());
    }
}
