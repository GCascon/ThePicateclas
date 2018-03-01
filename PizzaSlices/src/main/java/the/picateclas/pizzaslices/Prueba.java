package the.picateclas.pizzaslices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Prueba {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        float valor = (float) 2 / 3;
        System.out.println(valor);
        List<Integer> lista = new ArrayList<>();
        lista.add(3);
        lista.add(1);
        lista.add(8);

        Collections.sort(lista);

        System.out.println(lista);

        Collections.sort(lista, Collections.reverseOrder());

        System.out.println(lista);

    }

}
