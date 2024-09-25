import React from 'react'
import './footer.scss'
import { SlSocialFacebook, SlSocialGithub, SlSocialInstagram } from "react-icons/sl";
import { FaThreads } from "react-icons/fa6";
const Footer = () => {
  return (
    <div className='footer' id='footer'>
      <div className="footer-content">
        <div className="footer-content-left">
            <div className="footer-social-icons">
                    <FaThreads size={50}/>
                    <SlSocialFacebook size={50}/>
                    <SlSocialGithub size={50}/>
                    <SlSocialInstagram size={50}/>
            </div>
        </div>
        <div className="footer-content-center">
            <h2>COMPANY</h2>
            <ul>
                <li>Home</li>
                <li>About us</li>
                <li>Delivery</li>
                <li>Privacy policy</li>
            </ul>
        </div>
        <div className="footer-content-right">
            <h2>GET IN TOUCH</h2>
            <ul>
                <li>RikkeiSoft</li>
                <li>ducmh2@rikkeisoft.com</li>
            </ul>
        </div>
      </div>
      <hr />
    </div>
  )
}

export default Footer