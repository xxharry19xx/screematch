package com.aluracursos.screematch.principal;

import com.aluracursos.screematch.model.DatosEpisodio;
import com.aluracursos.screematch.model.DatosSerie;
import com.aluracursos.screematch.model.DatosTemporadas;
import com.aluracursos.screematch.model.Episodio;
import com.aluracursos.screematch.service.ConsumoApi;
import com.aluracursos.screematch.service.ConvierteDatos;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=92c025d0";

    private ConvierteDatos conversor = new ConvierteDatos();

    public void muestraElMenu(){
        System.out.println("Ingresa el nombre de la serie:");
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE+nombreSerie.replace(" ","+")+API_KEY);
        var datos = conversor.obetenerDatos(json, DatosSerie.class);
        System.out.println(datos);

        //Buscar los datos de todas las temporadas

        List<DatosTemporadas> temporadas = new ArrayList<>();
        for (int i = 1; i <= datos.totalDeTemporadas() ; i++) {
            json = consumoApi.obtenerDatos(URL_BASE+nombreSerie.replace(" ","+")+"&Season="+ i +API_KEY);
            var datosTemporadas = conversor.obetenerDatos(json,DatosTemporadas.class);
            temporadas.add(datosTemporadas);
        }
        //temporadas.forEach(System.out::println);

//        Otra forma mas sencilla de mostrar los titulos de todos los espisodios de las temporadas
        //temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        //convertir toda la informacion en una lista de tipo datosEpisodios
        List<DatosEpisodio> datosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()) // Obtiene un flujo de episodios de todas las temporadas
                .collect(Collectors.toList());

        //TOP 5 EPISODIOS
//        datosEpisodios.stream()
//                .filter(e -> !e.evaluacion().equalsIgnoreCase("N/A"))
//                .peek(e -> System.out.println("Primer Filtro de  (N/A)" + e))
//                .sorted(Comparator.comparing(DatosEpisodio::evaluacion).reversed())
//                .peek(e -> System.out.println("Segundo filtro ordenar de mayoor a menor"))
//                .limit(5)
//                .peek(e -> System.out.println())
//                .forEach(System.out::println);



        // conviertiendo los datos en tipo episodio
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(),d)))
                .collect(Collectors.toList());

        episodios.forEach(System.out::println);

        //busqueda de episodio apartir de la fecha
        System.out.println("Ingresar una fecha");
        var fecha = teclado.nextInt();

        //LocalDate.of(fecha, 1, 1): Crea una fecha específica utilizando el año proporcionado por el usuario,
        // con el mes y día fijados en enero 1.
        LocalDate fechaBusqueda = LocalDate.of(fecha, 1, 1);

        //DateTimeFormatter.ofPattern("dd/MM/yyyy"): Define un formato para mostrar fechas como día/mes/año.
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

//        episodios.stream()
//                //e.getFechaDeLanzamiento() != null: Verifica que el episodio tenga una fecha de lanzamiento válida (no sea null).
//                //e.getFechaDeLanzamiento().isAfter(fechaBusqueda): Verifica que la fecha de lanzamiento del episodio sea posterior a la fecha ingresada por el usuario (fechaBusqueda).
//                .filter(e -> e.getFechaDeLanzamiento() != null && e.getFechaDeLanzamiento().isAfter(fechaBusqueda))
//                .forEach(e -> System.out.println(
//                        "Temporada " + e.getTemporada() +
//                                "Episodio" + e.getTitulo() +
//                                "Fecha de Lamzamientio" + e.getFechaDeLanzamiento().format(dtf)
//                        ));

        //Buscar Los Episodios segun su nombre
//        System.out.println("Ingresa el nombre de la pelicula");
//        teclado.nextLine();  // Esto asegura que nextLine() no lea un salto de línea vacío
//        String titulutop = teclado.nextLine();
//
//        episodios.stream()
//                .filter(e -> e.getTitulo().equalsIgnoreCase(titulutop))
//                .forEach(e -> System.out.println(
//                        "Episodio" + e.getTitulo() +
//                                "Fecha de Lamzamientio" + e.getFechaDeLanzamiento().format(dtf)
//                ));


        // busca episodios por pedasos de titulo
        System.out.println("Ingresa el nombre de la pelicula");
        teclado.nextLine();  // Esto asegura que nextLine() no lea un salto de línea vacío
        String pedasoTitulo = teclado.nextLine();

        //Declara una variable de tipo Optional<Episodio> que almacenará el resultado de la búsqueda.
        Optional<Episodio> tituloBuscado = episodios.stream()

//              .contains(...): Verifica si el título del episodio contiene la subcadena buscada.
                //toUpperCase() convierte la cadena en mayusculas
                .filter(e -> e.getTitulo().toUpperCase().contains(pedasoTitulo.toUpperCase()))

                //Devuelve el primer episodio que cumpla con la condición del filtro.
                .findFirst();

        if (tituloBuscado.isPresent()){
            System.out.println("Titulo encontrado : ");
            System.out.println("los datos son : " + tituloBuscado.get());
        }else {
            System.out.println("el episodio no se encuentra presente");
        }

    }
}
