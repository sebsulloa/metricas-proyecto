package model.data_structures;

public class TablaHashLinearProbing<K extends Comparable<K>, V extends Comparable<V>> extends TablaHash<K,V> {

    private ILista<NodoTS<K,V>> listaNodos;

    public TablaHashLinearProbing(int tamInicial) {
        super(tamInicial);
        listaNodos = new ArregloDinamico<>(tamanoTabla);
        
        for(int i=1; i<=tamanoTabla; i++) {
            try {
                listaNodos.insertElement(null, i);
            } catch (PosException | NullException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void put(K key, V value) {
        int posicion = hash(key);
        try {
            NodoTS<K,V> nodo = listaNodos.getElement(posicion);
            if (nodo != null && !nodo.isEmpty()) {
                posicion = getNextEmpty(posicion);
            }

            NodoTS<K,V> nuevo = new NodoTS<K,V>(key, value);
            listaNodos.changeInfo(posicion, nuevo);
            tamanoAct++;

        } catch (PosException | VacioException | NullException e) {
            e.printStackTrace();
        }
        
        double factorCarga = (double)tamanoAct/tamanoTabla;
        if (factorCarga > 0.75) {
            rehash();
        }
    }

    @Override
    public V get(K key) {
        int posicion = hash(key);
        V retornar = null;
        boolean encontroNull = false;

        while(retornar == null && !encontroNull) {
            NodoTS<K,V> nodoActual;
            try {
                nodoActual = listaNodos.getElement(posicion);
                if(nodoActual == null) {
                    encontroNull = true;
                }
                else if (!nodoActual.isEmpty() && nodoActual.getKey().compareTo(key) == 0) {
                    retornar = nodoActual.getValue();
                }
                else {
                    posicion++;
                    if(posicion > tamanoTabla) {
                        posicion = 1;
                    }
                }
            } catch (PosException | VacioException e) {
                e.printStackTrace();
            }
        }
        return retornar;
    }

    @Override
    public V remove(K key) {
        int posicion = hash(key);
        V retornar = null;
        boolean encontroNull = false;

        try {
            while(retornar == null && !encontroNull) {
                NodoTS<K,V> nodoActual = listaNodos.getElement(posicion);
                if(nodoActual == null) {
                    encontroNull = true;
                }
                else if (!nodoActual.isEmpty() && nodoActual.getKey().compareTo(key) == 0) {
                    retornar = nodoActual.getValue();
                }
                else {
                    posicion++;
                    if(posicion > tamanoTabla) {
                        posicion = 1;
                    }
                }
            }

            if(retornar != null) {
                listaNodos.getElement(posicion).setEmpty();
                tamanoAct--;
            }
        }
        catch (PosException | VacioException e) {
            e.printStackTrace();
        }

        return retornar;
    }

    @Override
    public boolean contains(K key) {
        return get(key) != null;
    }

    @Override
    public ILista<K> keySet() {
        ILista<K> lista = new ArregloDinamico<>(1);
        try {
            for (int i=1; i <= tamanoTabla; i++) {
                NodoTS<K,V> nodo = listaNodos.getElement(i);
                if(nodo != null && !nodo.isEmpty()) {
                    lista.insertElement(nodo.getKey(), lista.size()+1);
                }
            }
        }
        catch (PosException | NullException | VacioException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public ILista<V> valueSet() {
        ILista<V> lista = new ArregloDinamico<>(1);
        try {
            for (int i=1; i <= tamanoTabla; i++) {
                NodoTS<K,V> nodo = listaNodos.getElement(i);
                if(nodo != null && !nodo.isEmpty()) {
                    lista.insertElement(nodo.getValue(), lista.size()+1);
                }
            }
        }
        catch (PosException | NullException | VacioException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public ILista<NodoTS<K,V>> darListaNodos() {
        ILista<NodoTS<K,V>> nodos = new ArregloDinamico<>(1);
        try {
            for (int i=1; i <= tamanoTabla; i++) {
                NodoTS<K,V> nodo = listaNodos.getElement(i);
                if(nodo != null && !nodo.isEmpty()) {
                    nodos.insertElement(nodo, nodos.size()+1);
                }
            }
        }
        catch (PosException | NullException | VacioException e) {
            e.printStackTrace();
        }
        return nodos;
    }

    private int getNextEmpty(int posicion) {
        int posicionRetornar = (posicion % tamanoTabla) + 1;
        try {
            while(listaNodos.getElement(posicionRetornar) != null && 
                  !listaNodos.getElement(posicion).isEmpty()) {
                posicionRetornar++;
                if(posicionRetornar > tamanoTabla) {
                    posicionRetornar = 1;
                }
            }
        } catch (PosException | VacioException e) {
            e.printStackTrace();
        }
        return posicionRetornar;
    }

    private void rehash() {
        try {
            ILista<NodoTS<K,V>> nodos = darListaNodos();
            
            tamanoAct = 0;
            tamanoTabla *= 2;
            tamanoTabla = nextPrime(tamanoTabla);
            listaNodos = new ArregloDinamico<>(tamanoTabla);
            
            for(int i=1; i<=tamanoTabla; i++) {
                listaNodos.insertElement(null, i);
            }
            
            for(int i=1; i <= nodos.size(); i++) {
                NodoTS<K,V> actual = nodos.getElement(i);
                put(actual.getKey(), actual.getValue());
            }
            cantidadRehash++;
        }
        catch (NullException | VacioException | PosException e) {
            e.printStackTrace();
        }
    }
}