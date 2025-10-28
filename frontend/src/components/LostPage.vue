<template>
  <div class="lost-container">

    <!-- ✅ Navbar -->
    <nav class="navbar">
      <div class="nav-logo">Lost & Found</div>
      <ul class="nav-links">
        <li class="active">Lost Items</li>
        <li @click="goTo('found')">Found Items</li>
        <li @click="goTo('chat')">Chat</li>
        <li class="logout" @click="logout">Logout</li>
      </ul>
    </nav>

    <!-- ✅ Search + Filter -->
    <div class="search-filter-bar">
      <input class="search-input" type="text" v-model="searchText" placeholder="Search item name..." />

      <select v-model="filterLocation" class="filter-select">
        <option value="">All Locations</option>
        <option v-for="loc in locations" :key="loc" :value="loc">{{ loc }}</option>
      </select>
    </div>

    <!-- ✅ Items Grid -->
    <div class="grid-container">

      <!-- ✅ Add New Item Card -->
      <div v-if="!showForm" class="card add-card" @click="showForm = true">
        <div class="plus">+</div>
        <p>Add Lost Item</p>
      </div>

      <!-- ✅ Add Form Card -->
      <div v-else class="card form-card">
        <h3 class="form-title">Add Lost Item</h3>

        <input class="input" v-model="itemName" placeholder="Item Name" />
        <textarea class="input area" v-model="description" placeholder="Description"></textarea>

        <select v-model="location" class="input">
          <option disabled value="">Select Location</option>
          <option v-for="loc in locations" :key="loc" :value="loc">
            {{ loc }}
          </option>
        </select>

        <input type="file" accept="image/*" @change="onFileSelect" class="input" />
        <img v-if="previewImage" :src="previewImage" class="preview"/>

        <div class="form-actions">
          <button class="btn" @click="submitLostItem" :disabled="loading">{{ loading ? "Uploading..." : "Submit" }}</button>
          <button class="cancel-btn" @click="cancelForm">Cancel</button>
        </div>

        <p v-if="message" class="msg">{{ message }}</p>
      </div>

      <!-- ✅ Item Cards -->
      <div 
        v-for="(item, index) in filteredItems" 
        :key="item.id" 
        class="card item-card"
        @click="openModal(item)"
      >

        <span class="badge">LOST</span>

        <!-- ✅ Posted by You Tag -->
        <span class="owner-badge" v-if="isPostedByYou(item)">You</span>

        <img :src="item.imagePath" class="item-img" />
        <h4>{{ item.itemName }}</h4>
        <p class="loc">📍 {{ item.location }}</p>
      </div>

    </div>

    <!-- ✅ Item Detail Modal -->
    <div v-if="selectedItem" class="modal-overlay" @click.self="closeModal">
      <div class="modal-card">
        <img :src="selectedItem.imagePath" class="modal-img"/>

        <h3>{{ selectedItem.itemName }}</h3>
        <p><b>Description:</b> {{ selectedItem.description }}</p>
        <p><b>Location:</b> {{ selectedItem.location }}</p>
        <p><b>Posted:</b> {{ formatTime(selectedItem.timestamp) }}</p>

        <h4>Contact:</h4>
        <p><b>Name:</b> {{ selectedItem.postedBy.name }}</p>
        <p><b>Email:</b> {{ selectedItem.postedBy.email }}</p>
        <p><b>Mobile:</b> {{ selectedItem.postedBy.mobile }}</p>

        <button class="close-btn" @click="closeModal">Close</button>
      </div>
    </div>

  </div>
</template>



<script setup>
import { ref, onMounted, computed } from "vue"
import api from "../axios"
import { useRouter } from "vue-router"

const router = useRouter()

// UI State
const showForm = ref(false)
const loading = ref(false)
const message = ref("")
const lostItems = ref([])
const selectedItem = ref(null)

// Search & Filter
const searchText = ref("")
const filterLocation = ref("")

// Form fields
const itemName = ref("")
const description = ref("")
const location = ref("")
const imageFile = ref(null)
const previewImage = ref(null)

// Dropdown values
const locations = [
  "LIBRARY","MAIN_CANTEEN","ASHWINS","RISHABHS","NILAVAN","GENTS_HOSTEL",
  "LADIES_HOSTEL","GROUND","CSE","IT","MECH","CIVIL","CHEM","BIO",
  "ECE","EEE","SNU","CLOCK_TOWER","FOUNTAIN"
]

// ✅ Your ImgBB Key
const IMGBB_API_KEY = "92ab366fb3260affed4dbbecd2434b29"


function goTo(page) { router.push(`/${page}`) }
function logout() { router.push("/logout") }


// ✅ Filtered Items
const filteredItems = computed(() => {
  return lostItems.value.filter(item =>
    (searchText.value === "" || item.itemName.toLowerCase().includes(searchText.value.toLowerCase())) &&
    (filterLocation.value === "" || item.location === filterLocation.value)
  )
})


// ✅ Format time
function formatTime(timestamp) {
  return new Date(timestamp).toLocaleString()
}


// ✅ Check if posted by you
function isPostedByYou(item) {
  return item.postedBy?.userName === sessionStorage.getItem("username")
}


// ✅ Modal
function openModal(item) { selectedItem.value = item }
function closeModal() { selectedItem.value = null }


// ✅ Image select
function onFileSelect(e) {
  imageFile.value = e.target.files[0]
  previewImage.value = URL.createObjectURL(imageFile.value)
}


// ✅ Upload to ImgBB
async function uploadImage() {
  const formData = new FormData()
  formData.append("image", imageFile.value)

  const url = `https://api.imgbb.com/1/upload?key=${IMGBB_API_KEY}`
  const res = await api.post(url, formData)
  return res.data.data.url
}


// ✅ Submit Lost Item
async function submitLostItem() {
  const postedBy = {
    userName: sessionStorage.getItem("username"),
    email: sessionStorage.getItem("email"),
    mobile: sessionStorage.getItem("mobile"),
    name: sessionStorage.getItem("name")
  }

  if (!postedBy.userName) {
    message.value = "⚠ Login required"
    router.push("/login")
    return
  }

  if (!itemName.value || !description.value || !location.value || !imageFile.value) {
    message.value = "⚠ Please fill all details"
    return
  }

  loading.value = true

  try {
    const imageUrl = await uploadImage()

    const body = {
      itemName: itemName.value,
      description: description.value,
      timestamp: new Date().toISOString(),
      status: "LOST",
      location: location.value,
      imagePath: imageUrl,
      postedBy
    }

    await api.post("/items/post", body)

    fetchLostItems()
    resetForm()
  } 
  catch (err) {
    message.value = "❌ Post failed"
  }

  loading.value = false
}


// ✅ Reset form
function cancelForm() { resetForm() }
function resetForm() {
  itemName.value = ""
  description.value = ""
  location.value = ""
  imageFile.value = null
  previewImage.value = null
  message.value = ""
  showForm.value = false
}


// ✅ Fetch items
async function fetchLostItems() {
  try {
    const res = await api.get("/items/lost")
    lostItems.value = res.data.items || []
  } 
  catch (err) {
    console.error("Failed to fetch items", err)
  }
}

onMounted(fetchLostItems)
</script>



<style scoped>
.lost-container {
  width: 100%;
  min-height: 100vh;
  background: var(--light-bg);
  display: flex;
  flex-direction: column;
}

/* ✅ Navbar */
.navbar {
  height: 70px;
  width: 100%;
  background: var(--primary);
  display: flex;
  align-items: center;
  padding: 0 32px;
}
.nav-logo { font-size: 22px; color: #fff; margin-right: auto; }
.nav-links { display: flex; gap: 28px; list-style: none; }
.nav-links li { cursor: pointer; color: #e9f0ff; font-weight: 600; }
.nav-links li.active { color: #fff; border-bottom: 3px solid #fff; }

.search-filter-bar {
  display: flex;
  gap: 14px;
  padding: 10px 22px;
}
.search-input, .filter-select {
  padding: 10px;
  border: 1.4px solid #ccd;
  border-radius: 8px;
}

/* ✅ Grid */
.grid-container {
  display: grid;
  gap: 22px;
  padding: 20px;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
}

/* ✅ Card */
.card {
  background: #fff;
  padding: 16px;
  border-radius: 10px;
  text-align: center;
  box-shadow: 0 3px 10px rgba(0,0,0,0.15);
  transition: 0.25s;
}
.card:hover { transform: translateY(-2px); }

/* Add Card */
.add-card {
  display: flex; align-items: center; justify-content: center;
  flex-direction: column; font-weight: 600;
}
.plus { font-size: 50px; line-height: 0; color: var(--primary); }

/* ✅ Item Card */
.item-card { position: relative; cursor: pointer; }
.item-img { width: 100%; height: 180px; object-fit: cover; border-radius: 8px; }

.owner-badge {
  position: absolute;
  left: 6px;
  top: 6px;
  font-size: 11px;
  background: #007c23;
  color: white;
  padding: 4px 8px;
  border-radius: 6px;
  font-weight: 700;
}

.badge {
  position: absolute;
  top: 6px;
  right: 6px;
  background: #d91818;
  color: white;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
}

/* Form */
.input { width: 100%; padding: 10px; margin-top: 8px; border-radius: 8px; border: 1.4px solid #ccd; }
.area { height: 60px; resize: none; }
.preview { width: 100%; margin-top: 10px; border-radius: 8px; }

.form-actions { display: flex; gap: 10px; margin-top: 12px; }
.btn { flex: 1; background: var(--primary); color: white; border-radius: 8px; padding: 10px; }
.cancel-btn { flex: 1; background: #ddd; border-radius: 8px; padding: 10px; }

/* ✅ Modal */
.modal-overlay {
  position: fixed;
  top:0; left:0;
  width:100%; height:100%;
  background: rgba(0,0,0,0.6);
  display:flex; justify-content:center; align-items:center;
}
.modal-card {
  background:white;
  width: 400px;
  padding: 16px;
  border-radius: 14px;
  animation: fadeIn .3s ease-out;
}
.modal-img {
  width: 100%;
  border-radius: 10px;
  margin-bottom: 12px;
}
.close-btn {
  background: var(--primary);
  width: 100%;
  margin-top: 10px;
  padding: 8px;
  border-radius: 8px;
  color:white;
}

@keyframes fadeIn { from {opacity:0} to {opacity:1} }
</style>
