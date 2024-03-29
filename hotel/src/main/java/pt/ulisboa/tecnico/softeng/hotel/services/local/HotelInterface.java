package pt.ulisboa.tecnico.softeng.hotel.services.local;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.joda.time.LocalDate;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;
import pt.ulisboa.tecnico.softeng.hotel.domain.Booking;
import pt.ulisboa.tecnico.softeng.hotel.domain.Hotel;
import pt.ulisboa.tecnico.softeng.hotel.domain.Room;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;
import pt.ulisboa.tecnico.softeng.hotel.services.local.dataobjects.HotelData;
import pt.ulisboa.tecnico.softeng.hotel.services.local.dataobjects.RoomBookingData;
import pt.ulisboa.tecnico.softeng.hotel.services.local.dataobjects.RoomData;

public class HotelInterface {

	@Atomic(mode = TxMode.WRITE)
	public static void deleteHotels() {
		for (Hotel hotel : FenixFramework.getDomainRoot().getHotelSet()) {
			hotel.delete();
		}
	}

	@Atomic(mode = TxMode.READ)
	public static List<HotelData> getHotels() {
		return FenixFramework.getDomainRoot().getHotelSet().stream().map(h -> new HotelData(h))
				.collect(Collectors.toList());
	}

	@Atomic(mode = TxMode.WRITE)
	public static void createHotel(HotelData hotelData) {
		new Hotel(hotelData.getCode(), hotelData.getName());
	}

	@Atomic(mode = TxMode.READ)
	public static HotelData getHotelDataByCode(String code) {
		Hotel hotel = getHotelByCode(code);

		if (hotel != null) {
			return new HotelData(hotel);
		}

		return null;
	}

	@Atomic(mode = TxMode.WRITE)
	public static void createRoom(String hotelCode, RoomData roomData) {
		new Room(getHotelByCode(hotelCode), roomData.getNumber(), roomData.getType());
	}

	@Atomic(mode = TxMode.READ)
	public static RoomData getRoomDataByNumber(String code, String number) {
		Room room = getRoomByNumber(code, number);
		if (room == null) {
			return null;
		}

		return new RoomData(room);
	}

	@Atomic(mode = TxMode.WRITE)
	public static void createBooking(String code, String number, RoomBookingData booking) {
		Room room = getRoomByNumber(code, number);
		if (room == null) {
			throw new HotelException();
		}

		new Booking(room, booking.getArrival(), booking.getDeparture());
	}

	@Atomic(mode = TxMode.WRITE)
	public static String reserveRoom(Room.Type type, LocalDate arrival, LocalDate departure, String adventureId) {
		Booking booking = getBooking4AdventureId(adventureId);
		if (booking != null) {
			return booking.getReference();
		}

		for (Hotel hotel : FenixFramework.getDomainRoot().getHotelSet()) {
			Room room = hotel.hasVacancy(type, arrival, departure);
			if (room != null) {
				Booking newBooking = room.reserve(type, arrival, departure);
				newBooking.setAdventureId(adventureId);
				return newBooking.getReference();
			}
		}
		throw new HotelException();
	}

	@Atomic(mode = TxMode.WRITE)
	public static String cancelBooking(String reference) {
		for (Hotel hotel : FenixFramework.getDomainRoot().getHotelSet()) {
			Booking booking = hotel.getBooking(reference);
			if (booking != null && booking.getCancellation() == null) {
				return booking.cancel();
			}
		}
		throw new HotelException();
	}

	@Atomic(mode = TxMode.READ)
	public static RoomBookingData getRoomBookingData(String reference) {
		for (Hotel hotel : FenixFramework.getDomainRoot().getHotelSet()) {
			for (Room room : hotel.getRoomSet()) {
				Booking booking = room.getBooking(reference);
				if (booking != null) {
					return new RoomBookingData(booking);
				}
			}
		}
		throw new HotelException();
	}

	@Atomic(mode = TxMode.WRITE)
	public static Set<String> bulkBooking(int number, LocalDate arrival, LocalDate departure) {
		if (number < 1) {
			throw new HotelException();
		}

		List<Room> rooms = getAvailableRooms(number, arrival, departure);
		if (rooms.size() < number) {
			throw new HotelException();
		}

		Set<String> references = new HashSet<>();
		for (int i = 0; i < number; i++) {
			references.add(rooms.get(i).reserve(rooms.get(i).getType(), arrival, departure).getReference());
		}

		return references;
	}

	static List<Room> getAvailableRooms(int number, LocalDate arrival, LocalDate departure) {
		List<Room> availableRooms = new ArrayList<>();
		for (Hotel hotel : FenixFramework.getDomainRoot().getHotelSet()) {
			availableRooms.addAll(hotel.getAvailableRooms(arrival, departure));
			if (availableRooms.size() >= number) {
				return availableRooms;
			}
		}
		return availableRooms;
	}

	private static Hotel getHotelByCode(String code) {
		return FenixFramework.getDomainRoot().getHotelSet().stream().filter(h -> h.getCode().equals(code)).findFirst()
				.orElse(null);
	}

	private static Room getRoomByNumber(String code, String number) {
		Hotel hotel = getHotelByCode(code);
		if (hotel == null) {
			return null;
		}

		Room room = hotel.getRoomByNumber(number);
		if (room == null) {
			return null;
		}
		return room;
	}

	private static Booking getBooking4AdventureId(String adventureId) {
		for (Hotel hotel : FenixFramework.getDomainRoot().getHotelSet()) {
			Booking booking = hotel.getBooking4AdventureId(adventureId);
			if (booking != null) {
				return booking;
			}
		}
		return null;
	}

}
