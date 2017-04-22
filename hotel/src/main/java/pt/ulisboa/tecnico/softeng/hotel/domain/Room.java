package pt.ulisboa.tecnico.softeng.hotel.domain;

import org.joda.time.LocalDate;
import pt.ulisboa.tecnico.softeng.hotel.exception.HotelException;

import java.util.HashSet;
import java.util.Set;

public class Room extends Room_Base {
	public static enum Type {
		SINGLE, DOUBLE
	}

	public Room(Hotel hotel, String number, Type type) {
		checkArguments(hotel, number, type);

		setNumber(number);
		setType(type);

		if (hotel.hasRoom(number))
			throw new HotelException();

		setHotel(hotel);
	}

	public void delete() {
		setHotel(null);
		this.getBookingSet().foreach(Booking::delete);
		super.deleteDomainObject();
	}

	private void checkArguments(Hotel hotel, String number, Type type) {
		if (hotel == null || number == null || number.trim().length() == 0 || type == null) {
			throw new HotelException();
		}

		if (!number.matches("\\d*")) {
			throw new HotelException();
		}
	}

	int getNumberOfBookings() {
		return this.getBookingSet().size();
	}

	boolean isFree(Type type, LocalDate arrival, LocalDate departure) {
		if (!type.equals(getType())) {
			return false;
		}

		for (Booking booking : this.getBookingSet()) {
			if (booking.conflict(arrival, departure)) {
				return false;
			}
		}

		return true;
	}

	public Booking reserve(Type type, LocalDate arrival, LocalDate departure) {
		if (type == null || arrival == null || departure == null) {
			throw new HotelException();
		}

		if (!isFree(type, arrival, departure)) {
			throw new HotelException();
		}

		Booking booking = new Booking(getHotel(), arrival, departure);
		this.addBooking(booking);

		return booking;
	}

	public Booking getBooking(String reference) {
		for (Booking booking : this.getBookingSet()) {
			if (booking.getReference().equals(reference)
					|| (booking.isCancelled() && booking.getCancellation().equals(reference))) {
				return booking;
			}
		}
		return null;
	}

	public Room getRoomByNumber(String number) {
		for (Room room : getHotel().getRoomSet()) {
			if (room.getNumber().equals(number))
				return room;
		}
		return null;
	}

}