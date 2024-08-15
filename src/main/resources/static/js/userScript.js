console.log("Application logged in...");

const toggleSideBar=()=>
{
	if($(".sidebar").is(":visible"))
	{
		//close sidebar
		$(".sidebar").css("display","none");
		$(".content").css("margin-left","0%");
		$(".fa-bars").css("display","inline-block");
	}
	else{
		//open sidebar
		$(".sidebar").css("display","block");
		$(".content").css("margin-left","18%");
		$(".fa-bars").css("display","none");
		
	}
}

const search=()=>{
	//console.log("search done.");
	let query=$("#search-input").val();
	let url=`http://localhost:8080/smartcontacts/user/search/${query}`;
	if(query=="")
	{
		$(".search-result").hide();
	}
	else{
		fetch(url).then((contacts)=>{
			return contacts.json();
		}).then((data)=>{
			let text=`<div class="list-group">`;
			if(data.length>0){
			data.forEach(contact => {
				text+=`<a href="/smartcontacts/user/contact/${contact.cid}" class="list-group-item list-group-item-check">${contact.name}</a>`
			});
			}
			else text+=`<h6 class="list-group-item list-group-item-check text-danger">No results found</h6>`
			text+=`</div>`;
			$(".search-result").html(text);
		})
		
		$(".search-result").show();
	}
}

const paymentStart=()=>{
	console.log("payment started...");
	let amount = $("#payment_field").val();
	
	if(amount==null || amount.trim()=='')
	swal("Invalid Payment Amount", "Amount cannot be empty...", "error");
	else{
		console.log(amount);
		// use jQuery AJAX to send request to create payment order
		$.ajax({
			url:'/smartcontacts/user/create_order',
			data:JSON.stringify({amount:amount,info:'order_request'}),
			contentType:'application/json',
			type:'POST',
			dataType:'json',
			success:function(response){
				// invoked when success
				console.log(response);
				
				if(response.status=='created'){
					//open payment form
					let options={
						key:response.apiKeyId,
						amount:response.amount,
						currency:'INR',
						name:'Smart Contacts Manager',
						description:'Donation',
						image:'https://learncodewithdurgesh.com/_next/image?url=%2F_next%2Fstatic%2Fmedia%2Flcwd_logo.45da3818.png&w=640&q=75',
						order_id:response.id,
						handler:function(response){
							console.log(response.razorpay_payment_id);
							console.log(response.razorpay_order_id);
							console.log(response.razorpay_signature);
							updatePaymentDetails(response.razorpay_payment_id, response.razorpay_order_id, "paid");
						},
						"prefill": { //Recommend using the prefill parameter to auto-fill customer's contact information especially their phone number
       					  "name": "", //Customer's name
      					  "email": "",
      					  "contact": "" //Provide the customer's phone number for better conversion rates 
    					},
    					"notes": {
					    "address": "Hinjewadi Phase 1, Pune, India"
					    },
					    "theme": {
					        "color": "#3399cc"
					    }
					};
					
					var rzp = new Razorpay(options);
					rzp.on('payment.failed', function(response){
						console.log(response.error.code);
						console.log(response.error.description);
						console.log(response.error.source);
						console.log(response.error.step);
						console.log(response.error.reason);
						console.log(response.error.metadata.order_id);
						console.log(response.error.metadata.payment_id);
						swal("OOPS! Payment failed", "Please try again later...", "error");
					});
					
					rzp.open();
				}
			},
			error:function(jqXHR, textStatus, errorThrown){
				//invoked when error
				console.error("Error Status:",textStatus+"\n"+errorThrown);
				console.log("Response Text:",jqXHR.responseText);
				swal("OOPs! Something went wrong...",jqXHR.responseText,"error");
			}
		})
	}
}

function updatePaymentDetails(payment_id, order_id, order_status){
	$.ajax({
			url:'/smartcontacts/user/update_order',
			data:JSON.stringify({payment_id:payment_id,order_id:order_id,status:order_status}),
			contentType:'application/json',
			type:'POST',
			dataType:'json',
			success:function(){
			swal("Payment success!", "Thanks for your contribution!", "success");
			},
			error:function(){
			swal("Payment capture status failed!", "Payment was successful, but we failed to capture it.\n Next steps - we will contact you regarding further info for the same", "info");
			}
	});
}

function uploadContactsFile() {
          $('#contactsUploadBtn').on('click', function() {
              const fileInput = $('#contactsFileInput')[0];
              const file = fileInput.files[0];
              const formData = new FormData();

              if (!file)
				  swal("No file found!", "Please select a file to upload...", "info");
              else{

              formData.append('contactsFile', file);

              $.ajax({
                  url: '/smartcontacts/user/upload-file',
                  type: 'POST',
                  data: formData,
                  processData: false,
                  contentType: false,
				  success: function(response, status, xhr) {
					if (xhr.status === 204)
					    swal("Upload successful!", "No records found in the input file...", "warning");
					else 
					    swal("Upload successful!", response, "success");		   
				  },
				  error: function(xhr, status, error) {
	                  //console.error(error);
	                  const errorMessage = xhr.responseText || "Upload failed due to server error.";
	                  swal("Upload failed!", errorMessage, "error");
				  }
              });
			 }
          });
}