package model.data_structures;

public abstract class TablaHash<K extends Comparable<K>, V extends Comparable<V>> implements ITablaSimbolos<K,V> {
    
    protected int tamanoAct;
    protected int tamanoTabla;
    protected int minicial;
    protected double cantidadRehash;
    
    public TablaHash(int tamInicial) {
        int m = nextPrime(tamInicial);
        minicial = m;
        tamanoAct = 0;
        tamanoTabla = m;
        cantidadRehash = 0;
    }

    @Override
    public boolean isEmpty() {
        return tamanoAct == 0;
    }

    @Override
    public int size() {
        return tamanoAct;
    }

    @Override
    public int hash(K key) {
        return Math.abs(key.hashCode() % tamanoTabla) + 1;
    }

    // Common utility methods
    protected static boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n > 1 && n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        
        for (int i = 5; i * i <= n; i = i + 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }

    protected static int nextPrime(int N) {
        if (N <= 1) return 2;
        
        int prime = N;
        boolean found = false;
        
        while (!found) {
            prime++;
            if (isPrime(prime)) {
                found = true;
            }
        }
        return prime;
    }

    // Common getters for metrics
    public int darMinicial() {
        return minicial;
    }

    public int darMfinal() {
        return tamanoTabla;
    }

    public String toString() {
        String retorno = "";
        retorno += "La cantidad de duplas: " + size();
        retorno += "\nEl m inicial es: " + minicial;
        retorno += "\nEl m final es: " + tamanoTabla;
        double tamañoCarga = (double)tamanoAct/tamanoTabla;
        retorno += "\nEl factor de carga es: " + String.format("%.2f", tamañoCarga);
        retorno += "\nLa cantidad de rehash es: " + cantidadRehash;
        return retorno;
    }
}