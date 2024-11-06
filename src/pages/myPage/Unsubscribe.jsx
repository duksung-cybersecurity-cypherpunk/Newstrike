import React, { useState } from "react";
import api from "../../api/api.js";
import { useNavigate } from "react-router-dom";

import {
  BodyDiv,
  WrapperDiv,
  LogoImg,
  Input,
} from "../../styles/Login/LoginSignUp.styled";

import {
  MainP,
  Links,
  ButtonDiv,
  Button,
  ModuleDiv,
} from "../../styles/Mypage_s/Attendance.styled";

import Comment from "../../components/mypage/UnregisterComment";
import Logo from "../../images/Logo.svg";

export default function Unsubscribe() {
  const navigate = useNavigate();
  const [vaildEmail, setVaildEmail] = useState(null);

  const [form, setForm] = useState({
    email: "",
  });

  const handleEmailChange = (e) => {
    const email = e.target.value;
    setForm({ ...form, email });

    if (email.trim() === "") {
      setVaildEmail(null);
    }
  };

  const handleUnsubscribeAPI = async () => {
    try {
      const url = `api/v1/mypage/unsubscribe?email=${form.email}`;

      const response = await api.post(
        url,
        {},
        {
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
      alert("구독 해지가 완료되었습니다.");
      navigate("/Mypage");
      console.log(response.data);
    } catch (error) {
      console.error(
        "구독해지 error",
        error.response ? error.response.data : error
      );
      alert("구독 신청을 하지 않은 이메일입니다.");
    }
  };

  return (
    <BodyDiv>
      <WrapperDiv height="67vh" justifyContent="start">
        <LogoImg src={Logo} marginBottom="0px" />
        <MainP color="#588539" fontWeight="600" fontSize="33px" margin="0">
          뉴스레터 구독해지
        </MainP>
        <ModuleDiv paddingTop="30px" height="450px">
          <Comment />
          <MainP
            color="#E74F4F"
            fontWeight="600"
            fontSize="17px"
            margin="10px 0 -2px 10px"
          >
            구독 해지
            <span style={{ color: "black", fontWeight: "600" }}>
              하시겠다면
            </span>
          </MainP>
          <MainP fontWeight="600" fontSize="17px" margin="0 0 10px 10px">
            아래에 구독 시 신청한 이메일을 작성해주세요.
          </MainP>
          <Input
            width="400px"
            height="60px"
            borderRadius="10px"
            style={{
              marginLeft: "10px",
              marginBottom: "20px",
              borderColor: "#588539",
            }}
            placeholder="ex) example@naver.com"
            id="email"
            type="email"
            value={form.email}
            onChange={handleEmailChange}
          />
          <ButtonDiv>
            <Links to="/Mypage">
              <Button
                backgroudColor="#588539"
                color="#FFFFFF"
                textDecoration="underline"
              >
                구독 유지하기
              </Button>
            </Links>
            <Button
              onClick={() => {
                handleUnsubscribeAPI();
              }}
            >
              구독 해지하기
            </Button>
          </ButtonDiv>
        </ModuleDiv>
      </WrapperDiv>
    </BodyDiv>
  );
}
