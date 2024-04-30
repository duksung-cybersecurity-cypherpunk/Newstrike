import React from "react";
import {
  BodyDiv,
  WrapperDiv,
  LogoImg,
  Form,
  Input,
  Submit,
  UnderDiv,
  Button,
  VaildP
} from "../styles/Login/LoginSignUp.styled";

import Logo from "../images/Logo.svg";


export default function SignUp() {
  return (
    <BodyDiv>
      <WrapperDiv>
        <LogoImg src={Logo} marginBottom="20px"/>

        <Form>
          <Input
            id="nickName"
            type="text"
            placeholder="닉네임"
            width="410.975px"
            height="53.82px"
            borderRadius="9.78px"
          />

          <Input
            id="email"
            type="email"
            placeholder="이메일"
            width="410.975px"
            height="53.82px"
            borderRadius="9.78px"
          />
          <UnderDiv
          justifyContent="end">
            <Button>중복 확인</Button>
            <Button>인증 요청</Button>
          </UnderDiv>
          

          <Input
            id="emailCheck"
            type="number"
            placeholder="이메일 인증번호"
            width="410.975px"
            height="53.82px"
            borderRadius="9.78px"
          />

          <UnderDiv
          justifyContent="space-between">
            <VaildP>인증번호가 올바르지 않습니다.</VaildP>
            <Button>인증 확인</Button>
          </UnderDiv>
          

          <Input
            id="password"
            type="password"
            placeholder="비밀번호 (숫자, 영문 8~12자리)"
            width="410.975px"
            height="53.82px"
            borderRadius="9.78px"
          />

          <UnderDiv>
            <VaildP>비밀번호를 영문, 숫자, 특수문자 포함 8~12자리로 설정해주세요.</VaildP>
          </UnderDiv>

          <Input
            id="passwordCheck"
            type="password"
            placeholder="비밀번호 확인"
            width="410.975px"
            height="53.82px"
            borderRadius="9.78px"
          />

          <UnderDiv>
            <VaildP>비밀번호가 일치하지 않습니다.</VaildP>
          </UnderDiv>

          <Submit 
            value="회원가입" 
            type="submit" 
            width="410.975px"
            height="53.82px"
            marginTop="60px"/>
        </Form>
        
          
      </WrapperDiv>
    </BodyDiv>
  );
}
