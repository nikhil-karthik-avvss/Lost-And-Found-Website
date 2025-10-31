import {createRouter, createWebHistory} from 'vue-router'
import LoginPage from '../components/LoginPage.vue'
import RegisterPage from '../components/RegisterPage.vue'
import HomePage from '../components/HomePage.vue'
import LogoutPage from '../components/LogoutPage.vue'
import LostPage from '../components/LostPage.vue'
import FoundPage from '../components/FoundPage.vue'
import ClaimedPage from '../components/ClaimedPage.vue'

const routes=[
    {path:'/',redirect:'/login'},
    {path:'/login',component:LoginPage},
    {path:'/register',component:RegisterPage},
    {path:'/home',component:HomePage},
    {path:'/logout',component:LogoutPage},
    {path:'/lost',component:LostPage},
    {path:'/found',component:FoundPage},
    {path:'/claimed',component:ClaimedPage}
]

const router=createRouter({
    history:createWebHistory(),
    routes
})

export default router