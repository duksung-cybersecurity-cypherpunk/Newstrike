import React, { useState } from 'react';
import {
  Div,
  BodyDiv,
  WrapperDiv,
  TextDivForm,
  RightDiv,
  Form,
  Input,
  TitleDiv,
  TextDiv,
  TextSpan,
  Button,
  UnderDiv,
  VaildP,
  SubmitButton,
} from '../../styles/Mypage_j/ChangeInfo.styled.jsx';

import Header from '../../components/Header.jsx';

export default function ChangeInfo() {
  //form 관리
  const [form, setForm] = useState({
    nickName: '',
    email: '',
    emailCheck: '',
    password: '',
    passwordCheck: '',
  });

  const [validEmail, setValidEmail] = useState(null);
  const [validEmailCheck, setValidEmailCheck] = useState(null);
  const [validPW, setValidPW] = useState(null);
  const [validPWCheck, setValidPWCheck] = useState(null);

  // 비밀번호 유효성 함수
  const handlePW = (e) => {
    const password = e.target.value;
    setForm({ ...form, password });

    if (password.trim() === '') {
      setValidPW(null);
    } else {
      const isValidPassword =
        /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&^#()])[A-Za-z\d@$!%*?&^#()]{8,12}$/.test(
          password
        );
      setValidPW(isValidPassword);
    }
  };

  // 비밀번호 확인 유효성 함수
  const hadlePWCheck = (e) => {
    const passwordCheck = e.target.value;
    setForm((prevForm) => ({ ...prevForm, passwordCheck }));

    if (passwordCheck.trim() === '') {
      setValidPWCheck(null);
    } else if (passwordCheck === form.password) {
      setValidPWCheck(true);
    } else {
      setValidPWCheck(false);
    }
  };

  return (
    <Div>
      <BodyDiv>
        {/* 네비게이션바 */}
        <Header></Header>

        <WrapperDiv>
          <TitleDiv>
            <TextSpan fontsize="25px" fontweight="600" color="#000000">
              정보 수정
            </TextSpan>
          </TitleDiv>
          <RightDiv>
            <Form>
              <TextDivForm>
                <TextDiv>
                  <TextSpan>닉네임</TextSpan>
                </TextDiv>
                <Input
                  id="nickName"
                  type="text"
                  placeholder="nick1"
                  width="410.975px"
                  height="53.82px"
                  borderRadius="9.78px"
                />
              </TextDivForm>

              <TextDivForm>
                <TextDiv>
                  <TextSpan>이메일</TextSpan>
                </TextDiv>
                <Input
                  id="email"
                  type="email"
                  placeholder="email@naver.com"
                  width="410.975px"
                  height="53.82px"
                  borderRadius="9.78px"
                />
              </TextDivForm>

              <UnderDiv margintop="5px" justifyContent="end">
                {/* <Button>중복 확인</Button> */}
                <Button backgroundcolor="#588539" color="#FFFFFF">
                  인증 요청
                </Button>
              </UnderDiv>

              <TextDivForm margintop="10px">
                <TextDiv>
                  <TextSpan>인증 번호</TextSpan>
                </TextDiv>
                <Input
                  id="emailCheck"
                  type="number"
                  placeholder="이메일 인증번호"
                  width="410.975px"
                  height="53.82px"
                  borderRadius="9.78px"
                />
              </TextDivForm>

              <UnderDiv margintop="5px" justifyContent="space-between">
                <VaildP>인증번호가 올바르지 않습니다.</VaildP>
                {/* <VaildP color="#588539">인증되었습니다.</VaildP> */}
                <Button backgroundcolor="#588539" color="#FFFFFF">
                  인증 확인
                </Button>
              </UnderDiv>

              <TextDivForm margintop="10px">
                <TextDiv>
                  <TextSpan>비밀번호</TextSpan>
                </TextDiv>
                <Input
                  id="password"
                  type="password"
                  onChange={handlePW}
                  placeholder="비밀번호"
                  width="410.975px"
                  height="53.82px"
                  borderRadius="9.78px"
                />
              </TextDivForm>

              <TextDivForm>
                <TextDiv>
                  <TextSpan>비밀번호 확인</TextSpan>
                </TextDiv>
                <Input
                  id="passwordCheck"
                  type="password"
                  onChange={hadlePWCheck}
                  placeholder="비밀번호 확인"
                  width="410.975px"
                  height="53.82px"
                  borderRadius="9.78px"
                />
              </TextDivForm>

              {validPWCheck === false ? (
                <UnderDiv>
                  <VaildP>비밀번호가 일치하지 않습니다.</VaildP>
                </UnderDiv>
              ) : null}

              <TextDivForm margintop="60px" justifycontent="end">
                <SubmitButton
                  value="수정"
                  type="submit"
                  width="410.975px"
                  height="53.82px"
                />
              </TextDivForm>
            </Form>
          </RightDiv>
        </WrapperDiv>
      </BodyDiv>
    </Div>
  );
}