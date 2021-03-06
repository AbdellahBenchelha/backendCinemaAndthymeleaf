package org.sid.cinema.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.sql.Date;
import java.util.Optional;

import javax.transaction.Transactional;

import org.sid.cinema.dao.CategorieRepository;
import org.sid.cinema.dao.CinemaRepository;
import org.sid.cinema.dao.FilmRepository;
import org.sid.cinema.dao.PlaceRepository;
import org.sid.cinema.dao.ProjectionRepository;
import org.sid.cinema.dao.SalleRepository;
import org.sid.cinema.dao.SeanceRepository;
import org.sid.cinema.dao.TicketRepository;
import org.sid.cinema.dao.VilleRepository;
import org.sid.cinema.entities.Categorie;
import org.sid.cinema.entities.Cinema;
import org.sid.cinema.entities.Film;
import org.sid.cinema.entities.Place;
import org.sid.cinema.entities.Projection;
import org.sid.cinema.entities.Salle;
import org.sid.cinema.entities.Seance;
import org.sid.cinema.entities.Ticket;
import org.sid.cinema.entities.Ville;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class CinemaInitServiceImpl implements ICinemaInititService{

	@Autowired
	private VilleRepository villeRepository;
	
	@Autowired
	private CinemaRepository cinemaRepository;
	
	@Autowired
	private SalleRepository salleRepository;
	
	@Autowired
	private PlaceRepository placeRepository;
	
	@Autowired
	private SeanceRepository seanceRepository;
	
	@Autowired
	private FilmRepository filmRepository;
	
	@Autowired
	private ProjectionRepository projectinoRepository;
	
	@Autowired
	private CategorieRepository categorieRepository;
	
	@Autowired
	private TicketRepository ticketRepository;

	
	@Override
	public void initVilles() {
		Stream.of("Casablanca","Paris","Marrakech","Toulouse").forEach(nameVille->{
		Ville ville=new Ville ();
		ville.setName(nameVille);
		villeRepository.save(ville);
			
			
		});
		
	}

	@Override
	public void initCinemas() {
		
		villeRepository.findAll().forEach(v->{
			
			Stream.of("Megarama","Pathé","UGC","MK2").forEach(nameCinema->{
				Cinema cinema=new Cinema ();
				cinema.setName(nameCinema);
				cinema.setNombreSalles(3+(int)(Math.random()*7));
				cinema.setVille(v);
				cinemaRepository.save(cinema);
				
				});	
			
		});
		
	}

	@Override
	public void initSalles() {

	cinemaRepository.findAll().forEach(cinema->{
		for(int i=0;i<cinema.getNombreSalles();i++)
		{
			Salle salle=new Salle();
			salle.setName("Salle"+(i+1));
			salle.setCinema(cinema);
			salle.setNombrePlace(15+(int)(Math.random()*20));
			salleRepository.save(salle);
			
		}
		
	});
		
	}

	@Override
	public void initPlaces() {
		salleRepository.findAll().forEach(salle->{
			for(int i=0;i<salle.getNombrePlace();i++)
			{
				Place place=new Place();
				place.setNumero(i+1);
				place.setSalle(salle);
				placeRepository.save(place);
				
				
			}
			
			
			
		});
		
	}

	@Override
	public void initSeances() {
		Stream.of("12:00","15:00","17:00","19:00","21:00").forEach(s->{
			DateFormat dateFormat=new SimpleDateFormat("HH:mm");
			Seance seance=new Seance();
			try {
				seance.setHeureDebut(dateFormat.parse(s));
				seanceRepository.save(seance);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		});
		
	}

	@Override
	public void initCategories() {
		
		Stream.of("Action","Drame","Guerre","Fantastique","Science-fiction","Thriller").forEach(cat->{
		Categorie categorie=new Categorie();
		categorie.setName(cat);
		categorieRepository.save(categorie);
			
		});
		
	}

	@Override
	public void initFilms() {
		double[ ] durees=new double [] {1,1.5,2,2.5,3};
		
		java.util.List<Categorie> categories= categorieRepository.findAll();
		Stream.of("12hommesencolere","ForrestGump","GreenBook","LaLigneVerte","LeParrain",
				"12hommesencolere","12hommesencolere").forEach(titreFilm->{
			
			Film film=new Film();
			film.setTitre(titreFilm);
			film.setDuree(durees[new Random().nextInt(durees.length)]);
			film.setPhoto(titreFilm.replaceAll(" ", "")+".jpg");
			film.setCategorie(categories.get(new Random().nextInt(categories.size())));
			filmRepository.save(film);
		});;
		
	}

	//@SuppressWarnings("deprecation")
	@Override
	public void initProjections() {
		double[]  prices = new double[] {30,50,60,70,90,100};
		java.util.List<Film> films=filmRepository.findAll();
		villeRepository.findAll().forEach(ville->{
			ville.getCinemas().forEach(cinema->{
				cinema.getSalles().forEach(salle->{
					int index=new Random().nextInt(films.size());
					Film film=films.get(index);
						seanceRepository.findAll().forEach(seance->{
							Projection projection=new Projection();
							projection.setDateProjection( new java.sql.Date(2020, 07, 11));
							projection.setFilm(film);
							projection.setPrix(prices[new Random().nextInt(prices.length)]);
							projection.setSalle(salle);
							projection.setSeance(seance);
							projectinoRepository.save(projection);
			
			
			});
				
	});	
});
			
			
		});
		
	}

	
	@Override
	public void initTickets() {
		projectinoRepository.findAll().forEach(p->{
		p.getSalle().getPlaces().forEach(place->{
			Ticket ticket=new Ticket();
			ticket.setPlace(place);
			ticket.setPrix(p.getPrix());
			ticket.setProjection(p);
			ticket.setReservee(false);
			ticketRepository.save(ticket);
		});
		});
		
	}

	@Override
	public Page<Projection> findPaginated(int pageNo, int pageSize) {
		 Pageable pageable = (Pageable) PageRequest.of(pageNo - 1, pageSize);
		 return this.projectinoRepository.findAll(pageable);
	}

	
}