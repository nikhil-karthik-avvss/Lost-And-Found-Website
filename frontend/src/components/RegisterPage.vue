<template>
  <div class="auth-container">
    <div class="auth-card">
      <h2 class="auth-title">Create Account</h2>

      <label class="field-label">Full Name</label>
      <input type="text" v-model="name" class="input" placeholder="Enter full name" />

      <label class="field-label">Username</label>
      <input type="text" v-model="username" class="input" placeholder="Enter username" />

      <label class="field-label">Email</label>
      <input type="email" v-model="email" class="input" placeholder="example@email.com" />

      <label class="field-label">Mobile</label>
      <input type="text" v-model="mobile" class="input" placeholder="Enter mobile number" />

      <label class="field-label">Password</label>
      <div class="password-field">
        <input
          :type="showPassword ? 'text' : 'password'"
          v-model="password"
          class="input"
          placeholder="Enter password"
        />
        <span class="password-toggle" @click="showPassword = !showPassword">
          {{ showPassword ? "🙈" : "👁️" }}
        </span>
      </div>

      <label class="field-label">Confirm Password</label>
      <div class="password-field">
        <input
          :type="showConfirmPassword ? 'text' : 'password'"
          v-model="confirmPassword"
          class="input"
          placeholder="Re-enter password"
        />
        <span class="password-toggle" @click="showConfirmPassword = !showConfirmPassword">
          {{ showConfirmPassword ? "🙈" : "👁️" }}
        </span>
      </div>

      <button v-if="!otpShown" class="btn" @click="showOTP">
        Register
      </button>

      <!-- OTP Section -->
      <div v-if="otpShown" class="otp-section">
        <label class="field-label">OTP</label>
        <input type="text" v-model="otp" class="input" placeholder="Enter OTP" />

        <button class="btn" @click="submitForm">
          Submit
        </button>
      </div>

      <p v-if="message" class="error-msg">{{ message }}</p>

      <p class="subtitle">
        Already have an account?
        <router-link to="/login" class="link">Login here</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import api from "../axios"
import { ref } from "vue"
import { useRouter } from 'vue-router'

const router=useRouter()

const name = ref("")
const username = ref("")
const email = ref("")
const mobile = ref("")
const password = ref("")
const confirmPassword = ref("")
const otp = ref("")
const otpShown = ref(false)
const message = ref("")

const showPassword = ref(false)
const showConfirmPassword = ref(false)

const generatedOtp=ref("")

async function showOTP() {
  if (!name.value || !username.value || !email.value || !mobile.value || !password.value || !confirmPassword.value) {
    message.value = "Please fill all fields."
    return
  }

  if (password.value !== confirmPassword.value) {
    message.value = "Passwords do not match."
    return
  }

  try {
    const response = await api.get(`/mail/otp/${email.value}`)
    generatedOtp.value = response.data.otp  // expect backend to send { otp: "123456" }
    otpShown.value = true
    message.value = "OTP sent to your email!"
  } catch(error) {
    message.value = "Failed to send OTP! Try again."
    console.error(error)
  }
}

async function submitForm() {
  if (!otp.value) {
    message.value = "Please enter OTP"
    return
  }

  if (otp.value === generatedOtp.value) {
    try {
      const response = await api.post(`/auth/register`, {
        userName:username.value,
        password:password.value,
        email:email.value,
        mobile:mobile.value,
        name:name.value
      })

      message.value = response.data // ✅ take backend string

      if (response.status === 201) { // ✅ success check
        setTimeout(() => {
          router.push("/login")
        }, 2000)
      }

    } catch (error) {
      message.value = error.response?.data || "Registration failed!"
    }

  } else {
    message.value = "OTPs mismatch!"
  }
}

</script>

<style scoped>
:root {
  --primary: #0d47a1;
  --primary-hover: #0b3e8a;
  --gradient: linear-gradient(to bottom right, #3b82f6, #1e3a8a);
  --radius: 12px;
}

.auth-container {
  height: 100vh;
  width: 100vw;
  background: var(--gradient);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.auth-card {
  background: #fff;
  padding: 34px 30px;
  border-radius: var(--radius);
  width: 100%;
  max-width: 420px;
  box-shadow: 0px 12px 30px rgba(0, 0, 0, 0.18);
  animation: fadeIn 0.5s ease-out;
}

.auth-title {
  text-align: center;
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 26px;
  color: var(--primary);
}

.field-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
}

.input {
  width: 100%;
  padding: 12px 14px;
  border-radius: var(--radius);
  border: 1.7px solid #d1d5db;
  outline: none;
  font-size: 15px;
  margin-bottom: 16px;
  transition: 0.25s;
}

.input:focus {
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(29, 78, 216, 0.25);
}

.password-field {
  position: relative;
}

.password-toggle {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
  font-size: 18px;
}

.btn {
  width: 100%;
  padding: 14px;
  border: none;
  background: var(--primary);
  border-radius: var(--radius);
  font-size: 16px;
  font-weight: 600;
  color: white;
  cursor: pointer;
  transition: 0.25s;
}

.btn:hover {
  background: var(--primary-hover);
  transform: translateY(-2px);
}

.otp-section {
  animation: fadeIn 0.4s ease-in-out;
}

.subtitle {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: #444;
}

.link {
  color: var(--primary);
  font-weight: 600;
  margin-left: 4px;
  cursor: pointer;
}

.link:hover {
  text-decoration: underline;
}

.error-msg {
  text-align: center;
  margin-top: 10px;
  color: red;
  font-weight: 600;
}

@keyframes fadeIn {
  0% { opacity: 0; transform: translateY(10px); }
  100% { opacity: 1; transform: translateY(0); }
}
</style>
