package net.ezra.ui.home






import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigation
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Divider
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import net.ezra.R
import net.ezra.navigation.ROUTE_ADD_PRODUCT
import net.ezra.navigation.ROUTE_BOOKINGS
import net.ezra.navigation.ROUTE_HOME
import net.ezra.navigation.ROUTE_LOGIN
import net.ezra.navigation.ROUTE_VIEW_PROD
import java.net.URLEncoder


data class Service(val name: String, val description: String, val price: String, val duration: String, val imageResId: Int)
data class SpecialOffer(val title: String, val description: String, val imageResId: Int)
data class Testimonial(val name: String, val feedback: String, val imageResId: Int)
data class Booking(val serviceName: String, val date: String, val time: String, val price: String)



@Composable
fun getService(): List<Service> {
    return listOf(
        Service("Haircut", "Professional haircut services.", "Shs1200", "30 mins", R.drawable.haircut),
        Service("Shave", "Clean and precise shaving.", "Shs800", "20 mins", R.drawable.shave),
        Service("Hair Coloring","Stylish hair coloring services.", "Shs1000", "1 hour", R.drawable.hair_coloring),
        Service("Beard Trim","Expert beard trimming.", "Shs500", "15 mins", R.drawable.beard_trim),
        Service("Facial","Relaxing facial treatments.", "Shs1500", "45 mins", R.drawable.facial),
        Service("Massage","Therapeutic massage sessions.", "Shs1000", "1 hour", R.drawable.massage)
    )
}


@Composable
fun ServiceItem(service: Service, navController: NavHostController) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .background(Color.White)
            .clickable {
                // Navigate to the detailed service screen
                navController.navigate("service_detail/${service.name}")
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = service.imageResId),
            contentDescription = service.name,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = service.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun SpecialOfferItem(offer: SpecialOffer) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .background(Color.LightGray, shape = MaterialTheme.shapes.small)
            .padding(8.dp)
            .clickable { /* Handle click event */ }
    ) {
        Image(
            painter = painterResource(id = offer.imageResId),
            contentDescription = offer.title,
            modifier = Modifier
                .size(128.dp)
                .padding(8.dp)
        )
        Text(
            text = offer.title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = offer.description,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun TestimonialItem(testimonial: Testimonial) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .clickable { /* Handle click event */ }
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = testimonial.imageResId),
            contentDescription = testimonial.name,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = testimonial.name,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = testimonial.feedback,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun BookingItem(service: Service, navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.LightGray)
            .padding(16.dp)
    ) {
        Text(text = "Book ${service.name}", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))


        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            // Navigate to AddProductScreen
            navController.navigate(ROUTE_ADD_PRODUCT)
        }) {
            Text("Book Now")
        }
    }
}



@Composable
fun PaymentScreen(serviceName: String, servicePrice: String) {
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCVC by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Payment for $serviceName", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = cardNumber,
            onValueChange = { cardNumber = it },
            label = { Text("Card Number") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = cardExpiry,
            onValueChange = { cardExpiry = it },
            label = { Text("Card Expiry (MM/YY)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = cardCVC,
            onValueChange = { cardCVC = it },
            label = { Text("Card CVC") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            // Handle payment logic here
            Toast.makeText(context, "Payment Successful for $serviceName", Toast.LENGTH_SHORT).show()
        }) {
            Text("Pay ${servicePrice}")
        }
    }
}



@Composable
fun ServiceDetailScreen(navController: NavHostController,serviceName: String) {
    // Fetch the service details based on the serviceName
    val service = getService().find { it.name == serviceName }

    service?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            Image(
                painter = painterResource(id = it.imageResId),
                contentDescription = it.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = it.name,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = it.description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Price: ${it.price}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Duration: ${it.duration}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            BookingItem(service = it, navController = navController)
        }
    } ?: run {
        Text(text = "Service not found",color = Color.Black)
    }
}



class BookingViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _bookings = MutableLiveData<List<Booking>>()
    val bookings: LiveData<List<Booking>> = _bookings

    init {
        fetchBookings()
    }

    fun addBooking(booking: Booking) {
        _bookings.value = _bookings.value.orEmpty() + booking
    }

    fun fetchBookings() {
        db.collection("bookings")
            .get()
            .addOnSuccessListener { result ->
                val bookingList = mutableListOf<Booking>()
                for (document in result) {
                    val serviceName = document.getString("serviceName") ?: ""
                    val date = document.getString("date") ?: ""
                    val time = document.getString("time") ?: ""
                    val price = document.getString("price") ?: ""
                    bookingList.add(Booking(serviceName, date, time, price))
                }
                _bookings.value = bookingList
            }
            .addOnFailureListener {
                // Handle error
            }
    }
}







@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ResourceAsColor")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {


    val services =  getService()
    val specialOffers = remember {
        listOf(
            SpecialOffer("Discount Haircut", "Get 20% off on your first haircut", R.drawable.haircut),
            SpecialOffer("Free Beard Trim", "Free beard trim with any haircut", R.drawable.beard_trim),
            SpecialOffer("Facial Offer", "Buy one facial, get one free", R.drawable.facial)
        )
    }

    val testimonials = remember {
        listOf(
            Testimonial("Stephen Njoroge", "Great service and friendly staff!", R.drawable.profile_pic_1),
            Testimonial("Simon Muchiri", "I love my new haircut. Highly recommend!", R.drawable.profile_pic_2),
            Testimonial("Peris", "Best salon experience ever!", R.drawable.profile_pic_3)
        )
    }

    var isDrawerOpen by remember { mutableStateOf(false) }

    val callLauncher: ManagedActivityResultLauncher<Intent, ActivityResult> =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { _ ->

        }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(modifier = Modifier.width(8.dp))

                    }
                },

                actions = {
                    IconButton(onClick = {
                        navController.navigate(ROUTE_LOGIN) {
                            popUpTo(ROUTE_HOME) { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF000000),
                    titleContentColor = Color.White
                )
            )
        },


        content = @Composable {
            Box(
                modifier = Modifier
                    .fillMaxSize()

            ) {



                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .background(Color(0xFFFFFFFF)),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.barber),
                        contentDescription = "Cover Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )



                    Text(
                        text = "Featured Services",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                    )
                    LazyRow(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(services) { service ->
                            ServiceItem(service = service ,navController = navController)
                        }
                    }
                    Text(
                        text = "Special Offers",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                    )
                    LazyRow(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(specialOffers) { offer ->
                            SpecialOfferItem(offer = offer)
                        }
                    }
                    Text(
                        text = "What Our Customers Say",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                    )
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        testimonials.forEach { testimonial ->
                            TestimonialItem(testimonial = testimonial)
                            Divider()
                        }
                    }


                    Spacer(modifier = Modifier.height(16.dp))





                }

            }

        },

        bottomBar = { BottomBar(navController = navController) }


    )


}






@Composable
fun BottomBar(navController: NavHostController) {
    val selectedIndex = remember { mutableStateOf(0) }
    BottomNavigation(
        elevation = 10.dp,
        backgroundColor = Color(0xFF000000)


    ) {

        BottomNavigationItem(icon = {
            Icon(imageVector = Icons.Default.Home,"", tint = Color.White)
        },
            label = { Text(text = "Home",color =  Color.White) }, selected = (selectedIndex.value == 0), onClick = {

            })



        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Default.Favorite, contentDescription = "", tint = Color.White) },
            label = { Text(text = "Bookings", color = Color.White) },
            selected = (selectedIndex.value == 2),
            onClick = {
                selectedIndex.value = 2
                navController.navigate(ROUTE_VIEW_PROD) {
                    popUpTo(ROUTE_HOME) { inclusive = true }
                }
            }
        )





        }


    }



