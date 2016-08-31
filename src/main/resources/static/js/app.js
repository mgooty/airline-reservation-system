$(document).ready(function() {

    $('#searchflights').on('click', function(event) {

        var fromdate = $('#fromdate').val();
        var todate = $('#todate').val();
        var fromcity = $('#fromcity').val();
        var tocity = $('#tocity').val();

        var tripclass = $('#trip-class').val();
        var returntri = $("#returntrip").is(':checked');
        var numseat = $('#numseat').val();


        var divgen = function(flightId, code, dtime, dur, price) {
            var timesp = moment(dtime).format("MM/DD/YYYY hh:mm A")
            return '<div class="panel  panel-primary"> <div class="panel-body"><h4 class="card-title"> <label class="checkbox-inline">' +
                '<input type="checkbox" class="onwardclass" id=' + flightId + '>Flight Code: ' + code + '</label></h4>' +
                '<p class="card-text">Departure Time: ' + timesp + '</p>' +
                ' <p class="card-text"> Duration in Minutes: ' + dur + '</p>' +
                ' <p class="card-text"> Price per Seat: ' + price + '</p> </div></div>';
        };

        $.ajax({
            url: 'flight?startDate=' + fromdate + '&endDate=' + todate + '&fromCity=' + fromcity + '&toCity=' + tocity + '&flightClass=' + tripclass +
                '&numOfSeats=' + numseat + '&onlyOnward=' + !returntri,
            type: 'GET',
            cache: false,
            dataType: 'json',
            processData: false,
            headers: {
            	"X-AUTH-TOKEN":localStorage.getItem("X-AUTH-TOKEN")
            },
            success: function(data, textStatus, jqXHR) {
                if (data) {
                    $('#searchpanel').attr("hidden", "hidden");
                    $('#searchresult').removeAttr("hidden");
                    if (data) {
                        for (i = 0; i < data.onwardFlights.length; i++) {
                            var onward = data.onwardFlights[i];
                            $('#on-detail').append(divgen(onward.id, onward.flightCode, onward.departureDate, onward.durationInMins, onward.pricePerSeat));
                        }
                        for (i = 0; i < data.returnFlights.length; i++) {
                            var onward = data.returnFlights[i];
                            $('#re-detail').append(divgen(onward.id, onward.flightCode, onward.departureDate, onward.durationInMins, onward.pricePerSeat));
                        }
                    }
                }
            },
            error: function(jqXHR, textStatus, errorThrown) {

            }
        });
    });

    var bookinin = {
        "numOfSeats": null,
        "onwardFlightId": null,
        "returnFlightId": null
    }
    var detail = function(id){
    	return '<div id='+ id+'  class="row"><div class="col-md-4 col-sm-6 col-xs-12" style="margin-top:20px"><div class="input-group"><span class="input-group-addon" id="basic-addon3">Name</span>' +
        '<input type="text" class="form-control uname" id=' + id+' aria-describedby="basic-addon3"></div></div>' +
        '<div class="col-md-4 col-sm-6 col-xs-12" style="margin-top:20px">' +
        '<div class="input-group ">' +
        '<span class="input-group-addon" id="basic-addon3">Age</span>' +
        '<input type="number" class="form-control paage" id="todate" aria-describedby="basic-addon3"></div></div>' +
        '<div class="col-md-4 col-sm-6 col-xs-12" style="margin-top:20px">' +
        '<div class="input-group "> <span class="input-group-addon" id="basic-addon3">Gender</span>' +
        '<select aria-describedby="basic-addon3" class="form-control" id="">'+
        '<option>MALE</option>' +
        '<option>FEMALE</option></select></div></div></div>';
    }

    var onwardBookingId;
    var returnBookingId;
    $('#proc-book').on('click', function(event) {
        bookinin.numOfSeats = $('#numseat').val();
        selected = [];
        $('#on-detail input[type=checkbox]').each(function() {
            if ($(this).is(":checked")) {
                selected.push($(this).attr('id'));
            }
        });
        bookinin.onwardFlightId = selected[0]

        selected = [];
        $('#re-detail input[type=checkbox]').each(function() {
            if ($(this).is(":checked")) {
                selected.push($(this).attr('id'));
            }
        });
        bookinin.returnFlightId = selected[0]
        $.ajax({
            url: 'flight/booking',
            type: 'POST',
            cache: false,
            data: JSON.stringify(bookinin),
            contentType: 'application/json',
            headers: {
            	"X-AUTH-TOKEN":localStorage.getItem("X-AUTH-TOKEN")
            },
            success: function(data, textStatus, jqXHR) {
                $('#searchresult').attr("hidden", "hidden");
                $('#booking-details').removeAttr("hidden");
                for (i = 0; i < data.numOfSeats; i++) {
                    $('#booking-details .panel-body').append(detail('us'+i));
                }
                onwardBookingId = data.onwardBookingId;
                returnBookingId = data.returnBookingId;
            },
            error: function(jqXHR, textStatus, errorThrown) {

            }
        });
    });

	var passdetail={
		"onwardBookingPassengerRecord": {
			"bookingId": null,
			"passengers": [
			               
			]
		},
		"returnBookingPassengerRecord": {
			"bookingId": null,
			"passengers": [
				
			]
		}
	}

    $('#proceed-pay').on('click', function(event) {
    	var pass={"name":null,"gender":null,"age":null};
    	var passengers=[];
    	$('#booking-details .row').each(function() {
            if ($(this)) {
	            var name= $(this).find('.uname').val();
	            var page= $(this).find('.paage').val();
	            var gen= $(this).find('select').val();
	            pass.name=name;
	            pass.gender=gen;
	            pass.age=page;
	            passengers.push(pass);
            }
    	});
    	passdetail.onwardBookingPassengerRecord.passengers=passengers;
    	passdetail.onwardBookingPassengerRecord.bookingId=onwardBookingId;
    	passdetail.returnBookingPassengerRecord.passengers=passengers;
    	passdetail.returnBookingPassengerRecord.bookingId=returnBookingId;
     
    	$.ajax({
            url: 'flight/booking/passengers',
            type: 'POST',
            cache: false,
            processData: false,
            data: JSON.stringify(passdetail),
            contentType: 'application/json',
            headers: {
            	"X-AUTH-TOKEN":localStorage.getItem("X-AUTH-TOKEN")
            },
            success: function(data, textStatus, jqXHR) {
                
            },
            error: function(jqXHR, textStatus, errorThrown) {
                
            }
        });
    });

	$('#login').on('click', function(event) {
		console.log("invoke authentication API");
		var email = $('#email').val();
		var password = $('#password').val();
		$.ajax({
            url: 'auth' + '?email=' + email + '&password=' + password,
            type: 'POST',
            cache: false,
            processData: false,
            contentType: 'application/json',
            success: function(data, textStatus, jqXHR) {
            	$('#loginpanel').attr("hidden", "hidden");
                $('#searchpanel').removeAttr("hidden");
                
                localStorage.setItem("X-AUTH-TOKEN", jqXHR.getResponseHeader('X-AUTH-TOKEN'));
            },
            error: function(jqXHR, textStatus, errorThrown) {
                
            }
        });
	});
});