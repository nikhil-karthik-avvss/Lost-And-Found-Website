<template>
  <div class="lost-container">

    <!-- ✅ Navbar -->
    <nav class="navbar">
      <div class="nav-logo">Lost & Found</div>
      <ul class="nav-links">
        <li @click="goTo('lost')">Lost Items</li>
        <li class="active">Found Items</li>
        <li @click="goTo('chat')">Chat</li>
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

    <!-- ✅ Items Grid -->
    <div class="grid-container">

      <!-- ✅ Add New -->
      <div v-if="!showForm" class="card add-card" @click="showForm = true">
        <div class="plus">+</div>
        <p>Add Found Item</p>
      </div>

      <!-- ✅ Add Form -->
      <div v-else class="card form-card">
        <h3>Add Found Item</h3>

        <input class="input" v-model="itemName" placeholder="Item Name" />
        <textarea class="input area" v-model="description" placeholder="Description"></textarea>

        <select v-model="location" class="input">
          <option disabled value="">Select Location</option>
          <option v-for="loc in locations" :key="loc" :value="loc">{{ loc }}</option>
        </select>

        <input type="file" class="input" @change="onFileSelect" />
        <img v-if="previewImage" :src="previewImage" class="preview"/>

        <div class="form-actions">
          <button class="btn" @click="submitItem" :disabled="loading">{{ loading ? "Uploading..." : "Submit" }}</button>
          <button class="cancel-btn" @click="resetForm">Cancel</button>
        </div>

        <p v-if="message" class="msg">{{ message }}</p>
      </div>

      <!-- ✅ Items -->
      <div 
        v-for="item in filteredItems" 
        :key="item.id" 
        class="card item-card"
        :class="{ claimed: item.status === 'CLAIMED' }"
        @click="openModal(item)"
      >
        <span class="badge" :class="item.status === 'CLAIMED' ? 'claimed-badge' : 'found-badge'">
          {{ item.status }}
        </span>

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
        <p><b>Posted:</b> {{ formatTime(selectedItem.timestamp) }}</p>

        <h4>Contact:</h4>
        <p><b>Name:</b> {{ selectedItem.postedBy.name }}</p>
        <p><b>Email:</b> {{ selectedItem.postedBy.email }}</p>
        <p><b>Mobile:</b> {{ selectedItem.postedBy.mobile }}</p>

        <!-- ✅ Owner Controls -->
        <div v-if="isPostedByYou(selectedItem)" class="owner-actions">

          <select v-model="selectedClaimUser" class="input" v-if="selectedItem.status !== 'CLAIMED'">
            <option value="">Select claimed user</option>
            <option 
              v-for="u in users"
              :key="u.id"
              :value="JSON.stringify(u)"
            >
              {{ u.name }} ({{ u.userName }})
            </option>
          </select>

          <button 
            v-if="selectedItem.status !== 'CLAIMED'" 
            class="claim-btn" 
            @click="markAsClaimed"
          >
            ✅ Mark as Claimed
          </button>

          <button class="delete-btn" @click="deleteItem">
            🗑 Delete
          </button>

        </div>

        <!-- ✅ Notify Button for Non-Owners -->
        <div v-else class="notify-section">
          <button class="notify-btn" @click="notifyOwner">
            🔔 Notify Founder
          </button>
        </div>

        <button class="close-btn" @click="closeModal">Close</button>
      </div>
    </div>

  </div>
</template>


<script setup>
import { ref, computed, onMounted } from "vue";
import api from "../axios";
import { useRouter } from "vue-router";

const router = useRouter();

// UI state
const showForm = ref(false);
const loading = ref(false);
const message = ref("");
const foundItems = ref([]);
const users = ref([]);
const selectedItem = ref(null);

// form fields
const itemName = ref("");
const description = ref("");
const location = ref("");
const imageFile = ref(null);
const previewImage = ref(null);

// claim user
const selectedClaimUser = ref("");

// filter
const searchText = ref("");
const filterLocation = ref("");

const locations = [
  "LIBRARY","MAIN_CANTEEN","ASHWINS","RISHABHS","NILAVAN","GENTS_HOSTEL",
  "LADIES_HOSTEL","GROUND","CSE","IT","MECH","CIVIL","CHEM","BIO",
  "ECE","EEE","SNU","CLOCK_TOWER","FOUNTAIN"
];

const IMGBB_API_KEY = "92ab366fb3260affed4dbbecd2434b29";

const filteredItems = computed(() =>
  foundItems.value.filter(item =>
    item.itemName.toLowerCase().includes(searchText.value.toLowerCase()) &&
    (filterLocation.value === "" || item.location === filterLocation.value)
  )
);

function formatTime(ts) {
  return new Date(ts).toLocaleString();
}
function goTo(p) { router.push(`/${p}`); }
function logout() { router.push("/logout"); }
function isPostedByYou(item) {
  return item.postedBy?.userName === sessionStorage.getItem("username");
}
function closeModal() {
  selectedItem.value = null;
}

async function openModal(item) {
  selectedItem.value = item;

  if (isPostedByYou(item)) {
    const res = await api.get("/auth/users");
    users.value = res.data.users.map(u => ({
      id: u.id,
      userName: u.userName,
      name: u.name,
      mobile: u.mobile,
      email: u.email
    }));
  }
}

function onFileSelect(e) {
  imageFile.value = e.target.files[0];
  previewImage.value = URL.createObjectURL(imageFile.value);
}

async function uploadImage() {
  const fd = new FormData();
  fd.append("image", imageFile.value);

  const res = await api.post(`https://api.imgbb.com/1/upload?key=${IMGBB_API_KEY}`, fd);
  return res.data.data.url;
}

async function submitItem() {
  if (!itemName.value || !description.value || !location.value || !imageFile.value) {
    message.value = "⚠ Fill all fields";
    return;
  }
  loading.value = true;

  const postedBy = {
    id: sessionStorage.getItem("id"),
    userName: sessionStorage.getItem("username"),
    name: sessionStorage.getItem("name"),
    email: sessionStorage.getItem("email"),
    mobile: sessionStorage.getItem("mobile")
  };

  try {
    const img = await uploadImage();
    await api.post("/items/post", {
      itemName: itemName.value,
      description: description.value,
      location: location.value,
      status: "FOUND",
      timestamp: new Date().toISOString(),
      imagePath: img,
      postedBy
    });
    fetchItems();
    resetForm();
  } catch {
    message.value = "❌ Failed";
  }
  loading.value = false;
}

async function fetchItems() {
  const res = await api.get("/items/found");
  foundItems.value = res.data.items || [];
}

async function markAsClaimed() {
  if (!selectedClaimUser.value) {
    alert("Select user");
    return;
  }
  const claimedBy = JSON.parse(selectedClaimUser.value);

  await api.post(`/items/claim/${selectedItem.value.id}`,  claimedBy );
  fetchItems();
  closeModal();
}

async function deleteItem() {
  if (!confirm("Delete item?")) return;
  await api.delete(`/items/delete/${selectedItem.value.id}`);
  fetchItems();
  closeModal();
}

async function notifyOwner() {
  try {
    const ownerUser = selectedItem.value.postedBy?.userName;

    if (!ownerUser) {
      alert("Owner username not found!");
      return;
    }

    if (!confirm("Send notification to this Founder?")) return;

    // ✅ include notifier details
    const notifier = {
      userName: sessionStorage.getItem("username"),
      name: sessionStorage.getItem("name"),
      email: sessionStorage.getItem("email"),
      mobile: sessionStorage.getItem("mobile"),
    };

    const payload = {
      item: selectedItem.value,
      notifier,  // send both
    };

    // ✅ call backend
    await api.post(`/items/notify/${ownerUser}`, payload);
    alert(`✅ Notification sent to ${selectedItem.value.postedBy.name}`);
  } catch (err) {
    console.error(err);
    alert("❌ Failed to send notification");
  }
}


function resetForm() {
  itemName.value = "";
  description.value = "";
  location.value = "";
  imageFile.value = null;
  previewImage.value = null;
  selectedClaimUser.value = "";
  showForm.value = false;
}

onMounted(fetchItems);
</script>


<style scoped>
.lost-container {
  width: 100%;
  min-height: 100vh;
  background: var(--light-bg);
  display: flex;
  flex-direction: column;
}

.navbar {
  background: var(--primary);
  width: 100%;
  height: 70px;
  padding: 0 32px;
  display: flex;
  align-items: center;
}
.nav-logo {
  color: white;
  margin-right: auto;
  font-size: 22px;
  font-weight: 700;
}
.nav-links {
  display: flex;
  gap: 28px;
  list-style: none;
}
.nav-links li {
  color: #e8f0ff;
  cursor: pointer;
  font-weight: 600;
}
.nav-links li.active {
  border-bottom: 3px solid #fff;
  color: #fff;
}

.search-filter-bar {
  display: flex;
  gap: 14px;
  padding: 10px 22px;
}
.input,
.search-input,
.filter-select {
  border: 1.4px solid #ccd;
  padding: 10px;
  border-radius: 8px;
  width: 100%;
}

.grid-container {
  padding: 20px;
  display: grid;
  gap: 22px;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
}

.card {
  padding: 16px;
  border-radius: 10px;
  background: #fff;
  text-align: center;
  box-shadow: 0 3px 10px rgba(0,0,0,.15);
  transition: .25s;
}
.card:hover { transform: translateY(-2px); }

.item-card {
  position: relative;
  cursor: pointer;
}
.item-card.claimed {
  opacity: .6;
}
.item-img {
  width: 100%;
  height: 180px;
  object-fit: cover;
  border-radius: 8px;
}

.badge {
  position: absolute;
  top: 6px;
  right: 6px;
  color: white;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
}
.found-badge {
  background: #1ea300;
}
.claimed-badge {
  background: #555;
}

.owner-badge {
  position: absolute;
  left: 6px;
  top: 6px;
  padding: 4px 8px;
  font-size: 11px;
  border-radius: 6px;
  font-weight: 700;
  background: #007c23;
  color: white;
}

.form-actions {
  display: flex;
  gap: 10px;
  margin-top: 12px;
}
.btn {
  flex: 1;
  background: var(--primary);
  color: white;
  border-radius: 8px;
  padding: 10px;
}
.cancel-btn {
  flex: 1;
  background: #ddd;
  border-radius: 8px;
  padding: 10px;
}

.modal-overlay {
  position: fixed;
  top:0; left:0;
  width:100%; height:100%;
  background: rgba(0,0,0,.6);
  display:flex;
  justify-content:center;
  align-items:center;
}
.modal-card {
  width: 400px;
  background: white;
  padding: 16px;
  border-radius: 14px;
}
.modal-img {
  border-radius: 10px;
  width: 100%;
  margin-bottom: 12px;
}

.owner-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 20px;
}
.claim-btn {
  background: #009933;
  color: white;
  padding: 10px;
  border-radius: 8px;
}
.delete-btn {
  background: #d00000;
  color: white;
  padding: 10px;
  border-radius: 8px;
}
.close-btn {
  background: var(--primary);
  width: 100%;
  margin-top: 10px;
  padding: 10px;
  color: white;
  border-radius: 8px;
}

.notify-section {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}
.notify-btn {
  background: #ff9800;
  color: white;
  border-radius: 8px;
  padding: 10px 18px;
  font-weight: 600;
  cursor: pointer;
  transition: 0.25s;
}
.notify-btn:hover {
  background: #e68900;
}

.area { height: 60px; }
.preview { width: 100%; margin-top: 10px; border-radius: 8px; }
</style>
