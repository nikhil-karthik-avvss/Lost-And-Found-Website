<template>
  <div class="lost-container">

    <!-- ✅ Navbar -->
    <nav class="navbar">
      <div class="nav-logo">Lost & Found</div>
      <ul class="nav-links">
        <li @click="goTo('lost')">Lost Items</li>
        <li @click="goTo('found')">Found Items</li>
        <li class="active">Claimed Items</li>
        <li class="logout" @click="logout">Logout</li>
      </ul>
    </nav>

    <!-- ✅ Search + Filter -->
    <div class="search-filter-bar">
      <input class="search-input" type="text" v-model="searchText" placeholder="Search item name..." />
      <select v-model="filterLocation" class="filter-select">
        <option value="">All Locations</option>
        <option v-for="loc in locations" :key="loc">{{ loc }}</option>
      </select>
    </div>

    <!-- ✅ Claimed Items Grid -->
    <div class="grid-container">
      <div
        v-for="item in filteredItems"
        :key="item.id"
        class="card item-card claimed"
        @click="openModal(item)"
      >
        <span class="badge claimed-badge">CLAIMED</span>
        <span class="owner-badge" v-if="isPostedByYou(item)">You</span>

        <img :src="item.imagePath" class="item-img" />
        <h4>{{ item.itemName }}</h4>
        <p class="loc">📍 {{ item.location }}</p>
      </div>
    </div>

    <!-- ✅ Modal -->
    <div v-if="selectedItem" class="modal-overlay" @click.self="closeModal">
      <div class="modal-card">
        <img :src="selectedItem.imagePath" class="modal-img" />
        <h3>{{ selectedItem.itemName }}</h3>

        <p><b>Description:</b> {{ selectedItem.description }}</p>
        <p><b>Location:</b> {{ selectedItem.location }}</p>
        <p><b>Posted on:</b> {{ formatTime(selectedItem.timestamp) }}</p>

        <!-- Posted By -->
        <h4>Posted By</h4>
        <p><b>Username:</b> {{ selectedItem.postedBy?.userName || "-" }}</p>
        <p><b>Name:</b> {{ selectedItem.postedBy?.name || "-" }}</p>
        <p><b>Email:</b> {{ selectedItem.postedBy?.email || "-" }}</p>
        <p><b>Mobile:</b> {{ selectedItem.postedBy?.mobile || "-" }}</p>

        <!-- Claimed By -->
        <div v-if="selectedItem.closedBy && (selectedItem.closedBy.name || selectedItem.closedBy.userName)">
          <h4>Claimed By</h4>
          <p><b>Username:</b> {{ selectedItem.closedBy?.userName || "-" }}</p>
          <p><b>Name:</b> {{ selectedItem.closedBy?.name || "-" }}</p>
          <p><b>Email:</b> {{ selectedItem.closedBy?.email || "-" }}</p>
          <p><b>Mobile:</b> {{ selectedItem.closedBy?.mobile || "-" }}</p>
        </div>

        <button class="close-btn" @click="closeModal">Close</button>
      </div>
    </div>

  </div>
</template>


<script setup>
import { ref, computed, onMounted } from "vue"
import api from "../axios"
import { useRouter } from "vue-router"

const router = useRouter()

// ✅ Reactive States
const claimedItems = ref([])
const selectedItem = ref(null)
const searchText = ref("")
const filterLocation = ref("")
const message = ref("")

// ✅ Locations
const locations = [
  "LIBRARY","MAIN_CANTEEN","ASHWINS","RISHABHS","NILAVAN","GENTS_HOSTEL",
  "LADIES_HOSTEL","GROUND","CSE","IT","MECH","CIVIL","CHEM","BIO",
  "ECE","EEE","SNU","CLOCK_TOWER","FOUNTAIN"
]

// ✅ Navigation
function goTo(page) { router.push(`/${page}`) }
function logout() { router.push("/logout") }

// ✅ Utility
function formatTime(ts) {
  return ts ? new Date(ts).toLocaleString() : "-"
}
function isPostedByYou(item) {
  return item?.postedBy?.userName === sessionStorage.getItem("username")
}

// ✅ Modal
function openModal(item) {
  selectedItem.value = item
}
function closeModal() {
  selectedItem.value = null
  message.value = ""
}

// ✅ Computed Filter
const filteredItems = computed(() => {
  return (claimedItems.value || []).filter(item =>
    (searchText.value === "" || (item.itemName || "").toLowerCase().includes(searchText.value.toLowerCase())) &&
    (filterLocation.value === "" || item.location === filterLocation.value)
  )
})

// ✅ Fetch Claimed Items
async function fetchClaimedItems() {
  try {
    const res = await api.get("/items/claimed")
    claimedItems.value = res.data.items || []
  } catch (err) {
    console.error("❌ Failed to fetch claimed items:", err)
  }
}

onMounted(fetchClaimedItems)
</script>


<style scoped>
/* ✅ Same clean styling as other pages */

.lost-container {
  width: 100%;
  min-height: 100vh;
  background: var(--light-bg);
  display: flex;
  flex-direction: column;
}

/* Navbar */
.navbar {
  height: 70px;
  width: 100;
  background: var(--primary);
  display: flex;
  align-items: center;
  padding: 0 32px;
}
.nav-logo { font-size: 22px; color: #fff; margin-right: auto; font-weight: 700; }
.nav-links { display: flex; gap: 28px; list-style: none; }
.nav-links li { cursor: pointer; color: #e9f0ff; font-weight: 600; }
.nav-links li.active { color: #fff; border-bottom: 3px solid #fff; }

/* Search + Filter */
.search-filter-bar { display: flex; gap: 14px; padding: 10px 22px; }
.search-input, .filter-select {
  padding: 10px;
  border: 1.4px solid #ccd;
  border-radius: 8px;
}

/* Grid */
.grid-container {
  display: grid;
  gap: 22px;
  padding: 20px;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
}

/* Cards */
.card {
  background: #fff;
  padding: 16px;
  border-radius: 10px;
  text-align: center;
  box-shadow: 0 3px 10px rgba(0,0,0,0.15);
  transition: 0.25s;
}
.card:hover { transform: translateY(-2px); }

.item-card {
  position: relative;
  cursor: pointer;
}
.item-card.claimed { opacity: 0.7; }

.item-img {
  width: 100%;
  height: 180px;
  object-fit: cover;
  border-radius: 8px;
}

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

/* Badge */
.badge {
  position: absolute;
  top: 6px;
  right: 6px;
  color: white;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
}
.claimed-badge {
  background: #555;
}

/* Modal */
.modal-overlay {
  position: fixed;
  top:0; left:0;
  width:100%; height:100%;
  background: rgba(0,0,0,0.6);
  display:flex; justify-content:center; align-items:center;
}
.modal-card {
  background:white;
  width: 420px;
  padding: 18px;
  border-radius: 14px;
  animation: fadeIn .25s ease-out;
}
.modal-img {
  width: 100%;
  border-radius: 10px;
  margin-bottom: 12px;
}
.close-btn {
  background: var(--primary);
  width: 100%;
  margin-top: 12px;
  padding: 10px;
  border-radius: 8px;
  color:white;
}

@keyframes fadeIn { from {opacity:0} to {opacity:1} }
</style>
