package com.android.quemeful_qr;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;


import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import java.util.Map;
import java.util.Random;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private String userFirstName = "";
    private String userLastName = "";

    // array of first names later used to select at random
    private String[] firstNames = {"Adaptable", "Adventurous", "Affectionate", "Agile", "Alert",
            "Altruistic", "Ambitious", "Amiable", "Amicable", "Amusing",
            "Articulate", "Assertive", "Astute", "Attentive", "Authentic",
            "Avid", "Bewitching", "Bizarre", "Blithe", "Bold",
            "Brash", "Brave", "Bright", "Brilliant", "Brisk",
            "Buoyant", "Calm", "Candid", "Capable", "Carefree",
            "Caring", "Cautious", "Charismatic", "Charming", "Cheerful",
            "Chic", "Chivalrous", "Classic", "Clever", "Collaborative",
            "Comical", "Committed", "Compassionate", "Competent", "Confident",
            "Conscientious", "Considerate", "Constant", "Contemplative", "Cool",
            "Cooperative", "Courageous", "Courteous", "Creative", "Crisp",
            "Critical", "Curious", "Daring", "Dashing", "Dauntless",
            "Dazzling", "Debonair", "Decent", "Decisive", "Dedicated",
            "Deep", "Defiant", "Deliberate", "Delicate", "Delightful",
            "Demure", "Dependable", "Descriptive", "Detailed", "Determined",
            "Devoted", "Dexterous", "Diligent", "Diplomatic", "Direct",
            "Discreet", "Dynamic", "Earnest", "Easygoing", "Eclectic",
            "Effective", "Efficient", "Elaborate", "Elegant", "Eloquent",
            "Empathetic", "Energetic", "Enigmatic", "Enterprising", "Enthusiastic",
            "Ephemeral", "Equitable", "Erudite", "Essential", "Ethical",
            "Euphoric", "Evenhanded", "Exacting", "Excellent", "Exceptional",
            "Exciting", "Exemplary", "Exhaustive", "Exotic", "Expansive",
            "Experienced", "Expert", "Extraordinary", "Exuberant", "Fabulous",
            "Fair", "Faithful", "Fanciful", "Fantastic", "Farsighted",
            "Fashionable", "Fastidious", "Fearless", "Feisty", "Fervent",
            "Fiery", "Fierce", "Fine", "Flamboyant", "Flawless",
            "Flexible", "Focused", "Forceful", "Foreseeing", "Forgiving",
            "Formal", "Fortuitous", "Frank", "Free", "Frequent",
            "Fresh", "Friendly", "Fulfilled", "Fun", "Funny",
            "Fussy", "Generous", "Gentle", "Genuine", "Gifted",
            "Giving", "Glamorous", "Glorious", "Good-natured", "Graceful",
            "Gracious", "Grand", "Grateful", "Great", "Gregarious",
            "Grounded", "Handsome", "Handy", "Happy", "Hardy",
            "Harmonious", "Heartfelt", "Hearty", "Helpful", "Heroic",
            "High-spirited", "Honest", "Honorable", "Hopeful", "Hospitable",
            "Humble", "Humorous", "Idealistic", "Imaginative", "Immaculate",
            "Impartial", "Impassioned", "Impeccable", "Important", "Impressive",
            "Incisive", "Inclusive", "Independent", "Indomitable", "Industrious",
            "Innovative", "Inquisitive", "Insightful", "Inspirational", "Inspiring",
            "Intelligent", "Intense", "Intentional", "Interesting", "Intrepid",
            "Inventive", "Involved", "Jolly", "Joyful", "Jubilant",
            "Keen", "Kind", "Knowledgeable", "Laudable", "Lavish",
            "Lawful", "Leaderly", "Learned", "Legal", "Legitimate",
            "Lenient", "Liberal", "Light-hearted", "Likable", "Literate",
            "Lively", "Logical", "Lovable", "Loved", "Loving",
            "Loyal", "Lucid", "Lucky", "Luminous", "Lush",
            "Luxurious", "Magical", "Magnanimous", "Magnetic", "Magnificent",
            "Majestic", "Major", "Mannerly", "Many-sided", "Marked",
            "Marvelous", "Masculine", "Massive", "Masterful", "Matchless",
            "Mature", "Maverick", "Maximal", "Meaningful", "Meditative",
            "Mellow", "Melodious", "Memorable", "Merciful", "Merry",
            "Methodical", "Meticulous", "Mighty", "Mindful", "Ministerial",
            "Mirthful", "Modern", "Modest", "Momentous", "Monumental",
            "Moral", "Motivated", "Moving", "Multifaceted", "Munificent",
            "Musical", "Mutual", "Mysterious", "Narrative", "Natural",
            "Neat", "Necessary", "Needed", "Neighborly", "Neutral",
            "New", "Nice", "Nifty", "Nimble", "Noble",
            "Nonchalant", "Nonjudgmental", "Nurturing", "Obedient", "Objective",
            "Observant", "Obtainable", "Open", "Open-minded", "Operative",
            "Opportunistic", "Optimal", "Optimistic", "Orderly", "Organic",
            "Organized", "Original", "Outgoing", "Outstanding", "Overcoming",
            "Painstaking", "Passionate", "Patient", "Patriotic", "Peaceful",
            "Pensive", "Perceptive", "Perfect", "Permissive", "Persistent",
            "Personable", "Persuasive", "Philanthropic", "Philosophical", "Pioneering",
            "Pithy", "Placid", "Plausible", "Playful", "Pleasant",
            "Pleased", "Pleasing", "Plucky", "Poetic", "Poised",
            "Polished", "Polite", "Popular", "Positive", "Powerful",
            "Practical", "Pragmatic", "Praiseworthy", "Precise", "Precocious",
            "Predominant", "Preeminent", "Preferable", "Premier", "Premium",
            "Prepared", "Present", "Prestigious", "Pretty", "Prevalent",
            "Priceless", "Principled", "Privileged", "Proactive", "Probable",
            "Procedural", "Prodigious", "Productive", "Professional", "Proficient",
            "Profound", "Progressive", "Prolific", "Prominent", "Promising",
            "Prompt", "Proper", "Propitious", "Prospective", "Prosperous",
            "Protective", "Proud", "Prudent", "Punctual", "Pure",
            "Purposeful", "Quaint", "Qualified", "Qualitative", "Quality",
            "Quicker", "Quiet", "Radiant", "Rapid", "Rational",
            "Realistic", "Reasonable", "Reassuring", "Receptive", "Reciprocal",
            "Recondite", "Refined", "Reflective", "Regal", "Regular",
            "Relevant", "Reliable", "Relieved", "Remarkable", "Renowned",
            "Resilient", "Resolute", "Resourceful", "Respectable", "Respectful",
            "Responsible", "Responsive", "Restful", "Reverent", "Revolutionary",
            "Rewarding", "Rich", "Right", "Righteous", "Rigorous",
            "Robust", "Romantic", "Rousing", "Rustic", "Sacred",
            "Safe", "Sagacious", "Saintly", "Salient", "Sane",
            "Satisfactory", "Satisfied", "Savvy", "Scholarly", "Scientific",
            "Scrupulous", "Seamless", "Seasoned", "Secure", "Sedulous",
            "Select", "Self-assured", "Self-reliant", "Sensible", "Sensitive",
            "Serene", "Serious", "Service-oriented", "Set", "Sharp",
            "Shining", "Shrewd", "Significant", "Silent", "Simple",
            "Sincere", "Skillful", "Sleek", "Smart", "Smiling",
            "Smooth", "Snappy", "Sociable", "Solid", "Sophisticated",
            "Sought-after", "Soulful", "Sound", "Special", "Spectacular",
            "Speedy", "Spontaneous", "Sporty", "Spotless", "Sprightly",
            "Stable", "Stalwart", "Stately", "Statuesque", "Steadfast",
            "Steady", "Stellar", "Stimulating", "Straightforward", "Strategic",
            "Striking", "Strong", "Studious", "Stunning", "Stupendous",
            "Sturdy", "Stylish", "Suave", "Sublime", "Substantial",
            "Subtle", "Successful", "Succinct", "Sufficient", "Suitable",
            "Sumptuous", "Super", "Superb", "Superior", "Supportive",
            "Sure", "Surprising", "Sustainable", "Svelte", "Swanky",
            "Sweet", "Sympathetic","HORNY","SEXY", "Systematic", "Tactful", "Talented",
            "Tangible", "Tasteful", "Teachable", "Temperate", "Tenacious",
            "Tender", "Terrific", "Thankful", "Therapeutic", "Thorough",
            "Thoughtful", "Thrifty", "Thrilling", "Tidy", "Timely",
            "Tolerant", "Top", "Top-notch", "Tough", "Trailblazing",
            "Tranquil", "Transcendent", "Trendy", "Tribal", "Trim",
            "True", "Trusting", "Trustworthy", "Truthful", "Ultimate",
            "Unassuming", "Unbelievable", "Understanding", "Undisputed", "Unforgettable",
            "Unique", "United", "Universal", "Upbeat", "Uplifting",
            "Upright", "Upstanding", "Useful", "User-friendly", "Valiant",
            "Valid", "Valorous", "Valuable", "Venerable", "Venerated",
            "Venturesome", "Veracious", "Verbal", "Versatile", "Versed",
            "Vibrant", "Vigilant", "Vigorous", "Virtuous", "Visionary",
            "Vital", "Vivacious", "Vivid", "Vocal", "Wanted",
            "Warm", "Warmhearted", "Watchful", "Welcoming", "Well-behaved",
            "Well-developed", "Well-established", "Well-informed", "Well-intentioned", "Well-known",
            "Well-made", "Well-rounded", "Well-spoken", "Well-thought-of", "Well-timed",
            "Whimsical", "Wholehearted", "Willing", "Wise", "Witty",
            "Wonderful", "Worldly", "Worthy", "Youthful", "Zealous"};

    // array of last names later used to select at random
    private String[] lastNames = {"Aardvark", "Albatross", "Alligator", "Alpaca", "Ant",
            "Anteater", "Antelope", "Ape", "Armadillo", "Donkey",
            "Baboon", "Badger", "Barracuda", "Bat", "Bear",
            "Beaver", "Bee", "Bison", "Boar", "Buffalo",
            "Butterfly", "Camel", "Capybara", "Caribou", "Cassowary",
            "Cat", "Caterpillar", "Cattle", "Chameleon", "Cheetah",
            "Chicken", "Chimpanzee", "Chinchilla", "Clam", "Cobra",
            "Cockroach", "Cod", "Coyote", "Crab", "Crane",
            "Crocodile", "Crow", "Cuckoo", "Deer", "Dinosaur",
            "Dog", "Dolphin", "Dove", "Dragonfly", "Duck",
            "Dugong", "Eagle", "Echidna", "Eel", "Eland",
            "Elephant", "Elk", "Emu", "Falcon", "Ferret",
            "Finch", "Fish", "Flamingo", "Fly", "Fox",
            "Frog", "Gazelle", "Gerbil", "Giraffe", "Gnat",
            "Gnu", "Goat", "Goose", "Goldfish", "Gorilla",
            "Grasshopper", "Grouse", "Guanaco", "Gull", "Hamster",
            "Hare", "Hawk", "Hedgehog", "Heron", "Herring",
            "Hippopotamus", "Hornet", "Horse", "Human", "Hummingbird",
            "Hyena", "Ibex", "Ibis", "Jackal", "Jaguar",
            "Jellyfish", "Kangaroo", "Kingfisher", "Koala", "Kookaburra",
            "Kouprey", "Kudu", "Lapwing", "Lark", "Lemur",
            "Leopard", "Lion", "Llama", "Lobster", "Locust",
            "Loris", "Louse", "Lyrebird", "Magpie", "Mallard",
            "Manatee", "Mandrill", "Mantis", "Marten", "Meerkat",
            "Mink", "Mole", "Mongoose", "Monkey", "Moose",
            "Mosquito", "Moth", "Mouse", "Mule", "Narwhal",
            "Newt", "Nightingale", "Octopus", "Okapi", "Opossum",
            "Oryx", "Ostrich", "Otter", "Owl", "Ox",
            "Oyster", "Panda", "Panther", "Parrot", "Partridge",
            "Peafowl", "Pelican", "Penguin", "Pheasant", "Pig",
            "Pigeon", "Pony", "Porcupine", "Porpoise", "Quail",
            "Quelea", "Quetzal", "Rabbit", "Raccoon", "Rail",
            "Ram", "Rat", "Raven", "Red deer", "Red panda",
            "Reindeer", "Rhinoceros", "Rook", "Salamander", "Salmon",
            "Sand Dollar", "Sandpiper", "Sardine", "Scorpion", "Seahorse",
            "Seal", "Shark", "Sheep", "Shrew", "Skunk",
            "Snail", "Snake", "Sparrow", "Spider", "Spoonbill",
            "Squid", "Squirrel", "Starling", "Stingray", "Stinkbug",
            "Stork", "Swallow", "Swan", "Tapir", "Tarsier",
            "Termite", "Tiger", "Toad", "Trout", "Turkey",
            "Turtle", "Viper", "Vulture", "Wallaby", "Walrus",
            "Wasp", "Weasel", "Whale", "Wildcat", "Wolf",
            "Wolverine", "Wombat", "Woodcock", "Woodpecker", "Worm",
            "Wren", "Yak", "Zebra", "Alpaca", "Angelfish",
            "Anglerfish", "Antelope", "Arctic Fox", "Arctic Wolf", "Arowana",
            "Barramundi", "Basilisk", "Bass", "Beetle", "Binturong",
            "Bird", "Bongo", "Bonito", "Bonobo", "Booby",
            "Budgerigar", "Buffalo", "Bulbul", "Bull", "Bullfrog",
            "Butterfly Fish", "Buzzard", "Caiman", "Calf", "Caracal",
            "Carp", "Catfish", "Cattle", "Chamois", "Cheetah",
            "Chickadee", "Chimpanzee", "Chinchilla", "Chipmunk", "Coati",
            "Cockatoo", "Codfish", "Coelacanth", "Condor", "Conure",
            "Cormorant", "Coyote", "Crab", "Crane", "Crawfish",
            "Cricket", "Crocodile", "Cuckoo", "Curlew", "Cuscus",
            "Cuttlefish", "Dachshund", "Dalmatian", "Damselfish", "Deer",
            "Dhole", "Dingo", "Discus", "Dodo", "Dogfish",
            "Dolphin", "Donkey", "Dormouse", "Dotterel", "Dove",
            "Dragonfly", "Drever", "Duck", "Dugong", "Dunlin",
            "Eagle", "Echidna", "Eel", "Eland", "Elephant",
            "Elephant Seal", "Elk", "Emu", "Falcon", "Ferret",
            "Finch", "Firefly", "Fish", "Flamingo", "Flounder",
            "Fly", "Fossa", "Fox", "Frigatebird", "Frog",
            "Galago", "Gallinule", "Gaur", "Gazelle", "Gecko",
            "Gerbil", "Gibbon", "Giraffe", "Gnat", "Gnu",
            "Goat", "Goldfish", "Goose", "Gopher", "Gorilla",
            "Grasshopper", "Grouse", "Guanaco", "Gull", "Guppy",
            "Haddock", "Halibut", "Hamster", "Hare", "Harrier",
            "Hawk", "Hedgehog", "Heron", "Herring", "Hippopotamus",
            "Hornet", "Horse", "Human", "Hummingbird", "Hyena",
            "Iguana", "Impala", "Indri", "Insect", "Jackal",
            "Jaguar", "Jellyfish", "Kakapo", "Kangaroo", "Kingfisher",
            "Kiwi", "Koala", "Kodkod", "Kookaburra", "Kouprey",
            "Kudu", "Langur", "Lapwing", "Lark", "Lemming",
            "Lemur", "Leopard", "Lion", "Llama", "Lobster",
            "Locust", "Loris", "Louse", "Lyrebird", "Macaw",
            "Magpie", "Mallard", "Manatee", "Mandrill", "Markhor",
            "Marten", "Meerkat", "Mink", "Mole", "Molly",
            "Mongoose", "Mongrel", "Monkey", "Moose", "Mosquito",
            "Moth", "Mouse", "Mule", "Narwhal", "Newt",
            "Nightingale", "Numbat", "Ocelot", "Octopus", "Okapi",
            "Olm", "Opossum", "Orangutan", "Oryx", "Ostrich",
            "Otter", "Owl", "Ox", "Oyster", "Panda",
            "Panther", "Parakeet", "Parrot", "Partridge", "Peacock",
            "Peafowl", "Pekingese", "Pelican", "Penguin", "Perch",
            "Peregrine", "Pheasant", "Pig", "Pigeon", "Pika",
            "Pike", "Piranha", "Platypus", "Pointer", "Pony",
            "Poodle", "Porcupine", "Porpoise", "Possum", "Prairie Dog",
            "Prawn", "Puffin", "Puma", "Quail"};

    // not an admin case
    private boolean isAdmin = false;

    /**
     * This onCreate method checks whether user with a unique id already exists in the firebase collection.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nologin);
        FirebaseApp.initializeApp(this);

        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(deviceId).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        Log.d(TAG, "User exists with ID: " + deviceId);
                        userFirstName = task.getResult().getString("firstName");
                        userLastName = task.getResult().getString("lastName");
                        isAdmin = Boolean.TRUE.equals(task.getResult().getBoolean("Admin"));

                        transitionToMainScreen();
                    } else {
                        Log.d(TAG, "No user found with ID: " + deviceId);
                        promptNewUser(db, deviceId);
                    }
                });

    }


    /**
     * This method is used to generate random profile pictures on start of app (login)
     * The method selects random first name and last name from the arrays defined above (as login),
     * then selects a random appearance type from the below defined arrays and generates a random avatar.
     * @param db The firebase collection for users
     * @param deviceId The user existing with unique id (the unique id is the device id for this project's case)
     */


    private void promptNewUser(FirebaseFirestore db, String deviceId) {
        setContentView(R.layout.nologin);
        userFirstName = getRandomName(firstNames); // calling getRandomName method
        userLastName = getRandomName(lastNames);

        findViewById(R.id.getStartedButton).setOnClickListener(view -> {
            Bitmap avatarBitmap = generateAvatar(256, 256);

            // Define the callback implementation
            UploadCallback callback = new UploadCallback() {
                @Override
                public void onSuccess(String downloadUrl) {
                    // Here, downloadUrl is the URL of the uploaded avatar image
                    // Now, create and add the new user to Firestore with the avatarUrl
                    Map<String, Object> newUser = new HashMap<>();
                    newUser.put("uid", deviceId);
                    newUser.put("firstName", userFirstName);
                    newUser.put("lastName", userLastName);
                    newUser.put("homePage", "");
                    newUser.put("contact", "");
                    newUser.put("bio", "");
                    newUser.put("avatarUrl", downloadUrl); // Use the download URL here
                    newUser.put("Admin", false);

                    // Add the new user to Firestore
                    db.collection("users").document(deviceId).set(newUser)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "New user added with name: " + userFirstName + " " + userLastName + " and avatar URL: " + downloadUrl);
                                transitionToMainScreen();
                            })
                            .addOnFailureListener(e -> Log.e(TAG, "Error adding new user", e));
                }

                @Override
                public void onFailure(Exception e) {
                    // Handle the failure, e.g., show an error message
                    Log.e(TAG, "Error uploading avatar image", e);
                }
            };

            // Call the method to save the image to Firebase Storage and pass the callback
            saveImageToFirebaseStorage(avatarBitmap, deviceId, callback);
        });
    }


    private Bitmap generateAvatar(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Random random = new Random();

        // Defining more options
        String[] skinColors = {"#FAD7A0", "#E0AC69", "#C68642", "#8D5524", "#F3C3B4", "#5C3836"};
        String[] eyeColors = {"#6B4226", "#487EB0", "#1B1464", "#23A455", "#723E98", "#50C878"};
        String[] shirtColors = {"#3498DB", "#E74C3C", "#2ECC71", "#F1C40F", "#8E44AD", "#D35400"};
        String[] hairColors = {"#A52A2A", "#000000", "#FFFFFF", "#808080", "#FFFF31", "#24FE41"};
        String[] backgroundColors = {"#FFFFFF", "#FFEE58", "#B2EBF2", "#FFAB91", "#EBDEF0", "#AED6F1"};

        // Background
        paint.setColor(Color.parseColor(backgroundColors[random.nextInt(backgroundColors.length)]));
        canvas.drawRect(0, 0, width, height, paint);

        // Skin (Head)
        paint.setColor(Color.parseColor(skinColors[random.nextInt(skinColors.length)]));
        canvas.drawOval(new RectF(width * 0.2f, height * 0.1f, width * 0.8f, height * 0.6f), paint);

        // Eyes
        paint.setColor(Color.parseColor(eyeColors[random.nextInt(eyeColors.length)]));
        canvas.drawCircle(width * 0.35f, height * 0.35f, width * 0.1f, paint);
        canvas.drawCircle(width * 0.65f, height * 0.35f, width * 0.1f, paint);

        // Shirt
        paint.setColor(Color.parseColor(shirtColors[random.nextInt(shirtColors.length)]));
        canvas.drawRect(new RectF(width * 0.1f, height * 0.6f, width * 0.9f, height * 0.9f), paint);

        // Hair
        paint.setColor(Color.parseColor(hairColors[random.nextInt(hairColors.length)]));
        canvas.drawRect(new RectF(width * 0.2f, height * 0.05f, width * 0.8f, height * 0.2f), paint);

        return bitmap;
    }


    /**
     * This method is used to get a random name from the array
     * @param names String array of names
     * @return Randomly selected name
     */
    private String getRandomName(String[] names) {
        Random random = new Random();
        return names[random.nextInt(names.length)];
    }




    private void saveImageToFirebaseStorage(Bitmap bitmapImage, String imageName, UploadCallback callback) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("avatars/" + imageName + ".jpg");

        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Image uploaded successfully
            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(downloadUri -> {
                // Here we get the download URL
                String downloadUrl = downloadUri.toString();
                callback.onSuccess(downloadUrl);
            }).addOnFailureListener(callback::onFailure);
        }).addOnFailureListener(callback::onFailure);
    }


    /**
     * This method on clicking on the custom navigation taskbar for the app screen and functions,
     * sets the contents to the layout in the activity_main which is the base layout.
     */
    private void transitionToMainScreen() {
        setContentView(R.layout.activity_main);
        initializeBottomNavigation();
    }

    /**
     * This method is used to create the customized bottom navigation bar.
     */
    private void initializeBottomNavigation() {
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        MeowBottomNavigation bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.baseline_dashboard_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.baseline_event_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.baseline_account_circle_24));

        bottomNavigation.setOnClickMenuListener(item -> {
            // Handle click
        });

        bottomNavigation.setOnShowListener(item -> {
            Fragment fragment = null;
            switch (item.getId()) {
                case 1:
                    fragment = isAdmin ? AdminDashboardFragment.newInstance() : new Home(deviceId, isAdmin);
                    break;
                case 2:
                    fragment = isAdmin ? new Home(deviceId, isAdmin) : new Events();
                    break;
                case 3:
                    fragment = Profile.newInstance();
                    break;
            }
            loadFragment(fragment);
        });

        bottomNavigation.setOnReselectListener(item -> {
            // Implement reselection logic here if needed, otherwise leave as no-op to prevent NullPointerException
        });

        bottomNavigation.show(1, true);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        Log.d(TAG, "FCM Token: " + token);
                    }
                });
    }


    /**
     * This method is used for fragment transitions.
     * @param fragment The fragment to be replaced with.
     */
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notifications not allowed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

