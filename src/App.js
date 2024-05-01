import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';

// Login-------------------------------------------------------------------
import Login from './pages/Login';
import SignUp from "./pages/SignUp"

//mainPage
import MainPage from './pages/MainPage';

// mypage_s
import Attendance from './pages/myPage/Attendance';
import Unregister from './components/mypage/Unregister';


function App() {
  return (
    <div>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Login />} />
          <Route path="/mainPage" element={<MainPage />} />
          <Route path="/SignUp" element={<SignUp />} />
          <Route path="/Mypage" element={<Attendance />} />
          <Route path="/Mypage/Unregister" element={<Unregister />} />
          
        </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;
