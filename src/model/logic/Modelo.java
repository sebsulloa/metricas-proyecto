package model.logic;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Comparator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import model.data_structures.ArregloDinamico;
import model.data_structures.Country;
import model.data_structures.Country.ComparadorXKm;
import model.data_structures.Edge;
import model.data_structures.GrafoListaAdyacencia;
import model.data_structures.ILista;
import model.data_structures.ITablaSimbolos;
import model.data_structures.Landing;
import model.data_structures.ListaEncadenada;
import model.data_structures.NodoTS;
import model.data_structures.NullException;
import model.data_structures.PilaEncadenada;
import model.data_structures.PosException;
import model.data_structures.TablaHashLinearProbing;
import model.data_structures.TablaHashSeparteChaining;
import model.data_structures.VacioException;
import model.data_structures.Vertex;
import model.data_structures.YoutubeVideo;
import utils.Ordenamiento;


/**
 * Definicion del modelo del mundo
 *
 */
public class Modelo {
	/**
	 * Atributos del modelo del mundo
	 */
	private ILista datos;
	
	private GrafoListaAdyacencia grafo;
	
	private ITablaSimbolos paises;
	
	private ITablaSimbolos points;
	
	private ITablaSimbolos landingidtabla;
	
	private ITablaSimbolos nombrecodigo;

	/**
	 * Constructor del modelo del mundo con capacidad dada
	 * @param tamano
	 */
	public Modelo(int capacidad)
	{
		datos = new ArregloDinamico<>(capacidad);
	}

	/**
	 * Servicio de consulta de numero de elementos presentes en el modelo 
	 * @return numero de elementos presentes en el modelo
	 */
	public int darTamano()
	{
		return datos.size();
	}


	/**
	 * Requerimiento buscar dato
	 * @param dato Dato a buscar
	 * @return dato encontrado
	 * @throws VacioException 
	 * @throws PosException 
	 */
	public YoutubeVideo getElement(int i) throws PosException, VacioException
	{
		return (YoutubeVideo) datos.getElement( i);
	}

	public String toString()
	{
		String fragmento="Info básica:";
		
		fragmento+= "\n El número total de conexiones (arcos) en el grafo es: " + grafo.edges().size();
		fragmento+="\n El número total de puntos de conexión (landing points) en el grafo: " + grafo.vertices().size();
		fragmento+= "\n La cantidad total de países es:  " + paises.size();
		Landing landing=null;
		try 
		{
			landing = (Landing) ((NodoTS) points.darListaNodos().getElement(1)).getValue();
			fragmento+= "\n Info primer landing point " + "\n Identificador: " + landing.getId() + "\n Nombre: " + landing.getName()
			+ " \n Latitud " + landing.getLatitude() + " \n Longitud" + landing.getLongitude();
			
			Country pais= (Country) ((NodoTS) paises.darListaNodos().getElement(paises.darListaNodos().size())).getValue();
			
			fragmento+= "\n Info último país: " + "\n Capital: "+ pais.getCapitalName() + "\n Población: " + pais.getPopulation()+
			"\n Usuarios: "+ pais.getUsers();
		} 
		catch (PosException | VacioException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return fragmento;

	}
	
	
	public String req1String(String punto1, String punto2)
	{
		ITablaSimbolos tabla= grafo.getSSC();
		ILista lista= tabla.valueSet();
		int max=0;
		for(int i=1; i<= lista.size(); i++)
		{
			try
			{
				if((int) lista.getElement(i)> max)
				{
					max= (int) lista.getElement(i);
				}
			}
			catch(PosException | VacioException  e)
			{
				System.out.println(e.toString());
			}
			
		}
		
		String fragmento="La cantidad de componentes conectados es: " + max;
		
		try 
		{
			String codigo1= (String) nombrecodigo.get(punto1);
			String codigo2= (String) nombrecodigo.get(punto2);
			Vertex vertice1= (Vertex) ((ILista) landingidtabla.get(codigo1)).getElement(1);
			Vertex vertice2= (Vertex) ((ILista) landingidtabla.get(codigo2)).getElement(1);
			
			int elemento1= (int) tabla.get(vertice1.getId());
			int elemento2= (int) tabla.get(vertice2.getId());
			
			if(elemento1== elemento2)
			{
				fragmento+= "\n Los landing points pertenecen al mismo clúster";
			}
			else
			{
				fragmento+= "\n Los landing points no pertenecen al mismo clúster";
			}
		} 
		catch (PosException | VacioException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return fragmento;
		
	}
	
	public String req2String()
	{
		String fragmento="";
		
		ILista lista= landingidtabla.valueSet();
		
		int cantidad=0;
		
		int contador=0;
		
		for(int i=1; i<= lista.size(); i++)
		{
			try 
			{
				if( ( (ILista) lista.getElement(i) ).size()>1 && contador<=10)
				{
					Landing landing= (Landing) ((Vertex) ((ILista) lista.getElement(i) ).getElement(1)).getInfo();
					
					for(int j=1; j<=((ILista) lista.getElement(i)).size(); j++)
					{
						cantidad+= ((Vertex) ((ILista) lista.getElement(i)).getElement(j)).edges().size();
					}
					
					fragmento+= "\n Landing " + "\n Nombre: " + landing.getName() + "\n País: " + landing.getPais() + "\n Id: " + landing.getId() + "\n Cantidad: " + cantidad;
					
					contador++;
				}
			}
			catch (PosException | VacioException e) 
			{
				e.printStackTrace();
			}
			
		}
		
		return fragmento;
		
	}
	
	public String req3String(String pais1, String pais2)
	{
		Country pais11= (Country) paises.get(pais1);
		Country pais22= (Country) paises.get(pais2);
		String capital1=pais11.getCapitalName();
		String capital2=pais22.getCapitalName();

		PilaEncadenada pila= grafo.minPath(capital1, capital2);

		float distancia=0;

		String fragmento="Ruta: ";

		float disttotal=0;
		
		double longorigen=0;
		double longdestino=0;
		double latorigen=0;
		double latdestino=0;
		String origennombre="";
		String destinonombre="";

		while(!pila.isEmpty())
		{
			Edge arco= ((Edge)pila.pop());

			if(arco.getSource().getInfo().getClass().getName().equals("model.data_structures.Landing"))
			{
				longorigen=((Landing)arco.getSource().getInfo()).getLongitude();
				latorigen=((Landing)arco.getSource().getInfo()).getLongitude();
				origennombre=((Landing)arco.getSource().getInfo()).getLandingId();
			}
			if(arco.getSource().getInfo().getClass().getName().equals("model.data_structures.Country"))
			{
				longorigen=((Country)arco.getSource().getInfo()).getLongitude();
				latorigen=((Country)arco.getSource().getInfo()).getLongitude();
				origennombre=((Country)arco.getSource().getInfo()).getCapitalName();
			}
			if (arco.getDestination().getInfo().getClass().getName().equals("model.data_structures.Landing"))
			{
				latdestino=((Landing)arco.getDestination().getInfo()).getLatitude();
				longdestino=((Landing)arco.getDestination().getInfo()).getLatitude();
				destinonombre=((Landing)arco.getDestination().getInfo()).getLandingId();
			}
			if(arco.getDestination().getInfo().getClass().getName().equals("model.data_structures.Country"))
			{
				longdestino=((Country)arco.getDestination().getInfo()).getLatitude();
				latdestino=((Country)arco.getDestination().getInfo()).getLatitude();
				destinonombre=((Country)arco.getDestination().getInfo()).getCapitalName();
			}

			distancia = distancia(longdestino,latdestino, longorigen, latorigen);
			fragmento+= "\n \n Origen: " +origennombre + "  Destino: " + destinonombre + "  Distancia: " + distancia;
			disttotal+=distancia;

		}

		fragmento+= "\n Distancia total: " + disttotal;	

		return fragmento;
		
	}
	
	public String req4String()
	{
		String fragmento="";
		ILista lista1= landingidtabla.valueSet();
		
		String llave="";
		
		int distancia=0;
		
		try
		{
			int max=0;
			for(int i=1; i<= lista1.size(); i++)
			{
				if(((ILista)lista1.getElement(i)).size()> max)
				{
					max= ((ILista)lista1.getElement(i)).size();
					llave= (String) ((Vertex)((ILista)lista1.getElement(i)).getElement(1)).getId();
				}
			}
			
			ILista lista2= grafo.mstPrimLazy(llave);
			
			ITablaSimbolos tabla= new TablaHashSeparteChaining<>(2);
			ILista candidatos= new ArregloDinamico<>(1);
			for(int i=1; i<= lista2.size(); i++)
			{
				Edge arco= ((Edge) lista2.getElement(i));
				distancia+= arco.getWeight();
				
				candidatos.insertElement(arco.getSource(), candidatos.size()+1);
				
				candidatos.insertElement(arco.getDestination(), candidatos.size()+1);
				
				tabla.put(arco.getDestination().getId(),arco.getSource() );
			}
			
			ILista unificado= unificar(candidatos, "Vertice");
			fragmento+= " La cantidad de nodos conectada a la red de expansión mínima es: " + unificado.size() + "\n El costo total es de: " + distancia;
			
			int maximo=0;
			int contador=0;
			PilaEncadenada caminomax=new PilaEncadenada();
			for(int i=1; i<= unificado.size(); i++)
			{

				PilaEncadenada path= new PilaEncadenada();
				String idBusqueda= (String) ((Vertex) unificado.getElement(i)).getId();
				Vertex actual;

				while( (actual= (Vertex) tabla.get(idBusqueda))!=null && actual.getInfo()!=null)
				{
					path.push(actual);
					idBusqueda= (String) ((Vertex)actual).getId();
					contador++;
				}
				
				if(contador>maximo)
				{
					caminomax=path;
				}
			}
			
			fragmento+="\n La rama más larga está dada por lo vértices: ";
			for(int i=1; i<=caminomax.size(); i++)
			{
				Vertex pop= (Vertex) caminomax.pop();
				fragmento+= "\n Id " + i + " : "+ pop.getId();
			}
		}
		catch (PosException | VacioException | NullException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(fragmento.equals(""))
		{	
			return "No hay ninguna rama";
		}
		else 
		{
			return fragmento;
		}
	}
	
	public ILista req5(String punto)
	{
		String codigo= (String) nombrecodigo.get(punto);
		ILista lista= (ILista) landingidtabla.get(codigo);
		
		ILista countries= new ArregloDinamico<>(1);
		try 
		{
			Country paisoriginal=(Country) paises.get(((Landing) ((Vertex)lista.getElement(1)).getInfo()).getPais());
			countries.insertElement(paisoriginal, countries.size() + 1);
		} 
		catch (PosException | VacioException | NullException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for(int i=1; i<= lista.size(); i++)
		{
			try 
			{
				Vertex vertice= (Vertex) lista.getElement(i);
				ILista arcos= vertice.edges();
				
				for(int j=1; j<= arcos.size(); j++)
				{
					Vertex vertice2= ((Edge) arcos.getElement(j)).getDestination();
					
					Country pais=null;
					if (vertice2.getInfo().getClass().getName().equals("model.data_structures.Landing"))
					{
						Landing landing= (Landing) vertice2.getInfo();
						pais= (Country) paises.get(landing.getPais());
						countries.insertElement(pais, countries.size() + 1);
						
						float distancia= distancia(pais.getLongitude(), pais.getLatitude(), landing.getLongitude(), landing.getLatitude());
							
						pais.setDistlan(distancia);
					}
					else
					{
						pais=(Country) vertice2.getInfo();
					}
				}
				
			} catch (PosException | VacioException | NullException e) 
			{
				e.printStackTrace();
			}
		}
		
		ILista unificado= unificar(countries, "Country");
		
		Comparator<Country> comparador=null;

		Ordenamiento<Country> algsOrdenamientoEventos=new Ordenamiento<Country>();

		comparador= new ComparadorXKm();

		try 
		{

			if (lista!=null)
			{
				algsOrdenamientoEventos.ordenarMergeSort(unificado, comparador, true);
			}	
		}
		catch (PosException | VacioException| NullException  e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return unificado;
		
		
	}
	
	public String req5String(String punto)
	{
		ILista afectados= req5(punto);
		
		String fragmento="La cantidad de paises afectados es: " + afectados.size() + "\n Los paises afectados son: ";
	
		for(int i=1; i<=afectados.size(); i++)
		{
			try {
				fragmento+= "\n Nombre: " + ((Country) afectados.getElement(i)).getCountryName() + "\n Distancia al landing point: " + ((Country) afectados.getElement(i)).getDistlan();
			} catch (PosException | VacioException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return fragmento;
		
		
	}
	
	public ILista unificar(ILista lista, String criterio) {
		ILista listaResultado = new ArregloDinamico(1);
		
		try {
			if (lista != null) {
				if (criterio.equals("Vertice")) {
					unificarVertices(lista, listaResultado);
				} else {
					unificarPaises(lista, listaResultado);
				}
			}
		} catch (PosException | VacioException | NullException e) {
			e.printStackTrace();
		}
		
		return listaResultado;
	}
	
	private void unificarVertices(ILista lista, ILista listaResultado) 
		throws PosException, VacioException, NullException {
		
		Comparator<Vertex<String, Landing>> comparador = new Vertex.ComparadorXKey();
		Ordenamiento<Vertex<String, Landing>> algsOrdenamientoEventos = new Ordenamiento<>();
		algsOrdenamientoEventos.ordenarMergeSort(lista, comparador, false);
		
		procesarVertices(lista, listaResultado, comparador);
	}
	
	private void unificarPaises(ILista lista, ILista listaResultado) 
		throws PosException, VacioException, NullException {
		
		Comparator<Country> comparador = new Country.ComparadorXNombre();
		Ordenamiento<Country> algsOrdenamientoEventos = new Ordenamiento<>();
		algsOrdenamientoEventos.ordenarMergeSort(lista, comparador, false);
		
		procesarPaises(lista, listaResultado, comparador);
	}
	
	private void procesarVertices(ILista lista, ILista listaResultado, Comparator<Vertex<String, Landing>> comparador) 
		throws PosException, VacioException, NullException {
		
		for (int i = 1; i <= lista.size(); i++) {
			Vertex actual = (Vertex) lista.getElement(i);
			Vertex siguiente = obtenerSiguienteVertice(lista, i);
			
			if (siguiente != null) {
				if (comparador.compare(actual, siguiente) != 0) {
					listaResultado.insertElement(actual, listaResultado.size() + 1);
				}
			} else {
				procesarUltimoVertice(lista, listaResultado, comparador, actual, i);
			}
		}
	}
	
	private void procesarPaises(ILista lista, ILista listaResultado, Comparator<Country> comparador) 
		throws PosException, VacioException, NullException {
		
		for (int i = 1; i <= lista.size(); i++) {
			Country actual = (Country) lista.getElement(i);
			Country siguiente = obtenerSiguientePais(lista, i);
			
			if (siguiente != null) {
				if (comparador.compare(actual, siguiente) != 0) {
					listaResultado.insertElement(actual, listaResultado.size() + 1);
				}
			} else {
				procesarUltimoPais(lista, listaResultado, comparador, actual, i);
			}
		}
	}
	
	private Vertex obtenerSiguienteVertice(ILista lista, int posicion) 
		throws PosException, VacioException {
		
		return posicion < lista.size() ? (Vertex) lista.getElement(posicion + 1) : null;
	}
	
	private Country obtenerSiguientePais(ILista lista, int posicion) 
		throws PosException, VacioException {
		
		return posicion < lista.size() ? (Country) lista.getElement(posicion + 1) : null;
	}
	
	private void procesarUltimoVertice(ILista lista, ILista listaResultado, 
		Comparator<Vertex<String, Landing>> comparador, Vertex actual, int posicion) 
		throws PosException, VacioException, NullException {
		
		Vertex anterior = posicion > 1 ? (Vertex) lista.getElement(posicion - 1) : null;
		
		if (anterior != null) {
			if (comparador.compare(anterior, actual) != 0) {
				listaResultado.insertElement(actual, listaResultado.size() + 1);
			}
		} else {
			listaResultado.insertElement(actual, listaResultado.size() + 1);
		}
	}
	
	private void procesarUltimoPais(ILista lista, ILista listaResultado,
		Comparator<Country> comparador, Country actual, int posicion) 
		throws PosException, VacioException, NullException {
		
		Country anterior = posicion > 1 ? (Country) lista.getElement(posicion - 1) : null;
		
		if (anterior != null) {
			if (comparador.compare(anterior, actual) != 0) {
				listaResultado.insertElement(actual, listaResultado.size() + 1);
			}
		} else {
			listaResultado.insertElement(actual, listaResultado.size() + 1);
		}
	}
	
	public ITablaSimbolos unificarHash(ILista lista)
	{

		Comparator<Vertex<String, Landing>> comparador=null;

		Ordenamiento<Vertex<String, Landing>> algsOrdenamientoEventos=new Ordenamiento<Vertex<String, Landing>>();;

		comparador= new Vertex.ComparadorXKey();
		
		ITablaSimbolos tabla= new TablaHashSeparteChaining<>(2);


		try 
		{

			if (lista!=null)
			{
				algsOrdenamientoEventos.ordenarMergeSort(lista, comparador, false);

				for(int i=1; i<=lista.size(); i++)
				{
					Vertex actual= (Vertex) lista.getElement(i);
					Vertex siguiente= (Vertex) lista.getElement(i+1);

					if(siguiente!=null)
					{
						if(comparador.compare(actual, siguiente)!=0)
						{
							tabla.put(actual.getId(), actual);
						}
					}
					else
					{
						Vertex anterior= (Vertex) lista.getElement(i-1);

						if(anterior!=null)
						{
							if(comparador.compare(anterior, actual)!=0)
							{
								tabla.put(actual.getId(), actual);
							}
						}
						else
						{
							tabla.put(actual.getId(), actual);
						}
					}

				}
			}
		} 
		catch (PosException | VacioException| NullException  e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tabla;
	}

	public void cargar() throws IOException {
        // Initialize structures
        grafo = new GrafoListaAdyacencia(2);
        paises = new TablaHashLinearProbing(2);
        points = new TablaHashLinearProbing(2);
        landingidtabla = new TablaHashSeparteChaining(2);
        nombrecodigo = new TablaHashSeparteChaining(2);

        cargarPaises();
        cargarPuntosConexion();
        cargarConexiones();
        procesarConexionesAdicionales();
    }

    private void cargarPaises() throws IOException {
        Reader in = new FileReader("./data/countries.csv");
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader().parse(in);

        for (CSVRecord record : records) {
            if (!record.get(0).equals("")) {
                String countryName = record.get(0);
                String capitalName = record.get(1);
                double latitude = Double.parseDouble(record.get(2));
                double longitude = Double.parseDouble(record.get(3));
                String code = record.get(4);
                String continentName = record.get(5);
                float population = Float.parseFloat(record.get(6).replace(".", ""));
                double users = Double.parseDouble(record.get(7).replace(".", ""));

                Country pais = new Country(countryName, capitalName, latitude, longitude, 
                                         code, continentName, population, users);
                grafo.insertVertex(capitalName, pais);
                paises.put(countryName, pais);
            }
        }
    }

    private void cargarPuntosConexion() throws IOException {
        Reader in = new FileReader("./data/landing_points.csv");
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader().parse(in);

        for (CSVRecord record : records) {
            String landingId = record.get(0);
            String id = record.get(1);
            String[] x = record.get(2).split(", ");
            String name = x[0];
            String paisnombre = x[x.length-1];
            double latitude = Double.parseDouble(record.get(3));
            double longitude = Double.parseDouble(record.get(4));

            Landing landing = new Landing(landingId, id, name, paisnombre, latitude, longitude);
            points.put(landingId, landing);
        }
    }

    private void cargarConexiones() throws IOException {
        Reader in = new FileReader("./data/connections.csv");
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader().parse(in);

        for (CSVRecord record : records) {
            String origin = record.get(0);
            String destination = record.get(1);
            String cableid = record.get(3);
            String length = record.get(4).split(" ")[0];

            Landing landing1 = (Landing) points.get(origin);
            if (landing1 != null) {
                grafo.insertVertex(landing1.getLandingId() + cableid, landing1);
                Vertex vertice1 = grafo.getVertex(landing1.getLandingId() + cableid);

                Landing landing2 = (Landing) points.get(destination);
                if (landing2 != null) {
                    grafo.insertVertex(landing2.getLandingId() + cableid, landing2);
                    Vertex vertice2 = grafo.getVertex(landing2.getLandingId() + cableid);

                    procesarConexionesPaises(landing1, landing2, cableid);
                    agregarConexionLandings(landing1, landing2, cableid);
                    actualizarTablas(landing1, landing2, vertice1, vertice2);
                }
            }
        }
    }

    private void procesarConexionesPaises(Landing landing1, Landing landing2, String cableid) {
        Country pais1 = null;
        Country pais2 = null;
        String nombrepais1 = landing1.getPais();
        String nombrepais2 = landing2.getPais();

        if (nombrepais1.equals("Côte d'Ivoire")) {
            pais1 = (Country) paises.get("Cote d'Ivoire");
        } else if (nombrepais2.equals("Côte d'Ivoire")) {
            pais2 = (Country) paises.get("Cote d'Ivoire");
        } else {
            pais1 = (Country) paises.get(nombrepais1);
            pais2 = (Country) paises.get(nombrepais2);
        }

        if (pais1 != null) {
            float weight = distancia(pais1.getLongitude(), pais1.getLatitude(), 
                                  landing1.getLongitude(), landing1.getLatitude());
            grafo.addEdge(pais1.getCapitalName(), landing1.getLandingId() + cableid, weight);
        }

        if (pais2 != null) {
            float weight2 = distancia(pais2.getLongitude(), pais2.getLatitude(), 
                                   landing1.getLongitude(), landing1.getLatitude());
            grafo.addEdge(pais2.getCapitalName(), landing2.getLandingId() + cableid, weight2);
        }
    }

    private void agregarConexionLandings(Landing landing1, Landing landing2, String cableid) {
        Edge existe1 = grafo.getEdge(landing1.getLandingId() + cableid, landing2.getLandingId() + cableid);

        if (existe1 == null) {
            float weight3 = distancia(landing1.getLongitude(), landing1.getLatitude(),
                                   landing2.getLongitude(), landing2.getLatitude());
            grafo.addEdge(landing1.getLandingId() + cableid, landing2.getLandingId() + cableid, weight3);
        } else {
            float weight3 = distancia(landing1.getLongitude(), landing1.getLatitude(),
                                   landing2.getLongitude(), landing2.getLatitude());
            float peso3 = existe1.getWeight();

            if (weight3 > peso3) {
                existe1.setWeight(weight3);
            }
        }
    }

    private void actualizarTablas(Landing landing1, Landing landing2, Vertex vertice1, Vertex vertice2) {
        try {
            actualizarTablaLanding(landing1, vertice1);
            actualizarTablaLanding(landing2, vertice2);
            actualizarTablaNombre(landing1);
        } catch (PosException | NullException e) {
            e.printStackTrace();
        }
    }

    private void actualizarTablaLanding(Landing landing, Vertex vertice) throws PosException, NullException {
        ILista elementopc = (ILista) landingidtabla.get(landing.getLandingId());
        if (elementopc == null) {
            ILista valores = new ArregloDinamico(1);
            valores.insertElement(vertice, valores.size() + 1);
            landingidtabla.put(landing.getLandingId(), valores);
        } else {
            elementopc.insertElement(vertice, elementopc.size() + 1);
        }
    }

    private void actualizarTablaNombre(Landing landing) {
        if (nombrecodigo.get(landing.getLandingId()) == null) {
            nombrecodigo.put(landing.getName(), landing.getLandingId());
        }
    }

    private void procesarConexionesAdicionales() {
        try {
            ILista valores = landingidtabla.valueSet();
            
            for (int i = 1; i <= valores.size(); i++) {
                for (int j = 1; j <= ((ILista) valores.getElement(i)).size(); j++) {
                    if ((ILista) valores.getElement(i) != null) {
                        Vertex vertice1 = (Vertex) ((ILista) valores.getElement(i)).getElement(j);
                        for (int k = 2; k <= ((ILista) valores.getElement(i)).size(); k++) {
                            Vertex vertice2 = (Vertex) ((ILista) valores.getElement(i)).getElement(k);
                            grafo.addEdge(vertice1.getId(), vertice2.getId(), 100);
                        }
                    }
                }
            }
        } catch (PosException | VacioException e) {
            e.printStackTrace();
        }
    }

	private static float distancia(double lon1, double lat1, double lon2, double lat2) 
	{

		double earthRadius = 6371; // km

		lat1 = Math.toRadians(lat1);
		lon1 = Math.toRadians(lon1);
		lat2 = Math.toRadians(lat2);
		lon2 = Math.toRadians(lon2);

		double dlon = (lon2 - lon1);
		double dlat = (lat2 - lat1);

		double sinlat = Math.sin(dlat / 2);
		double sinlon = Math.sin(dlon / 2);

		double a = (sinlat * sinlat) + Math.cos(lat1)*Math.cos(lat2)*(sinlon*sinlon);
		double c = 2 * Math.asin (Math.min(1.0, Math.sqrt(a)));

		double distance = earthRadius * c;

		return (int) distance;

	}


}
