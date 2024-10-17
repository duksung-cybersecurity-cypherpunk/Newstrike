import styled from "styled-components";

const flexCenter = () => `
  display: flex;
  justify-content: center;
`;

export const Div = styled.div`
  height: 100vh;

  ${flexCenter()}
`;

export const BodyDiv = styled.div`
  width: 90vw;
  height: 100vh;
  /* background-color: beige; */
`;

export const Form = styled.form`
  width: 80%;
  height: 1220px;
  margin: 20px auto;

  /* background-color: lightblue; */
`;

export const NewsDivChat = styled.div`
  width: 100%;
  /* height: auto; */
  margin-top: 15px;
  margin-bottom: 13px;

  display: flex;
  justify-content: space-evenly; //NewsDiv와 ChatbotDiv

  /* background-color: lightgrey; */
`;

// 뉴스레터 영역
export const NewsDiv = styled.div`
  width: 45%;
  height: 850px;
  border-radius: 12px;
  padding-bottom: 40px;

  max-height: 780px;
  overflow-y: auto;

  // 스크롤바 보이지 않도록 설정
  ::-webkit-scrollbar {
    width: 0;
  }
  scrollbar-width: none; //Firefox에서의 스크롤바 숨김
  -ms-overflow-style: none; //IE 및 Edge에서의 스크롤바 숨김

  background-color: #cddfab;
`;

export const NewsWrapperDiv = styled.div`
  width: 100%;
  height: auto;
  margin-top: ${(props) => props.margintop || "80px"};

  ${flexCenter()}/* background-color: lightyellow; */
`;

export const NewsTitleWrapperDivDate = styled.div`
  width: 80%;
  height: auto;
  height: auto;

  /* background-color: lightseagreen; */
`;

export const Textspan = styled.span`
  display: ${(props) => props.display || "block"};
  font-size: ${(props) => props.fontsize || "17px"};
  font-weight: ${(props) => props.fontweight || "500"};
  text-align: ${(props) => props.textalign || "left"};

  margin-bottom: ${(props) => props.marginbottom || "8px"};

  /* background-color: green; */
`;

export const NewsImg = styled.img`
  width: 80%;
  height: auto;
  border: solid 0;
`;

export const NewsContentDiv = styled.div`
  width: 80%;
  height: auto;

  /* background-color: lightsalmon; */
`;

// 챗봇 영역
export const ChatbotDiv = styled.div`
  width: 45%;
  height: 820px;

  display: flex;
  justify-content: space-between;
  flex-direction: column;

  /* background-color: khaki; */
`;

export const AllMessageDiv = styled.div`
  width: 100%;
  height: 790px;

  display: flex;
  align-items: center;
  flex-direction: column;

  //스크롤
  max-height: 720px;
  overflow-y: auto;

  // 스크롤바 보이지 않도록 설정
  ::-webkit-scrollbar {
    width: 0;
  }
  scrollbar-width: none; //Firefox에서의 스크롤바 숨김
  -ms-overflow-style: none; //IE 및 Edge에서의 스크롤바 숨김

  z-index: 2;
`;

export const MessageDiv = styled.div`
  width: 96%;
  height: ${(props) => props.height || "auto"};
  margin-top: ${(props) => props.margintop || "25px"};

  display: flex;
  justify-content: ${(props) => props.justifycontent || "start"};
  /* align-items: center; */
  flex-direction: ${(props) => props.flexdirection || "row"};

  /* background-color: greenyellow; */
`;

export const ChatbotImg = styled.img`
  width: ${(props) => props.width || "40px"};
  height: ${(props) => props.height || "40px"};
  margin-bottom: 5px;

  border: solid 0;

  cursor: ${(props) => props.cursor || ""};
`;

export const SendspeechbubbleDiv = styled.div`
  width: auto;
  height: ${(props) => props.height || "auto"};
  border: solid 0;
  border-radius: 13px;
  padding: 3px 20px 3px 20px;

  display: flex;
  align-items: center;

  background-color: #588539;
`;

export const BotspeechbubbleDiv = styled.div`
  /* width: ${(props) => props.width || "375px"}; */
  height: auto;
  border: solid 0;
  border-radius: 16px;
  padding: 17px 20px 17px 20px;

  word-wrap: break-word;

  background-color: #cddfab;
`;

export const AllChattingDiv = styled.div`
  width: 100%;
  height: 45px;

  /* background-color: antiquewhite; */

  z-index: 2;
`;

export const ChattingDiv = styled.div`
  width: 95%;
  height: 43px;
  border: solid 1px;
  border-radius: 10px;
  padding-right: 10px;
  margin-left: 10px;

  display: flex;
  justify-content: space-between;
  align-items: center;

  background-color: #cddfab;
`;

export const ChattingInput = styled.input`
  width: 80%;
  height: 26px;
  margin-left: 15px;
  border: none;
  outline: none;

  font-size: 15px;

  background-color: #cddfab;
`;

// 로딩 애니메이션
export const DotsLoader = styled.div`
  display: flex;
  align-items: center;
  /* justify-content: center; */
  width: 100px;
  height: 20px;
`;

export const Dot = styled.div`
  width: 5px;
  height: 5px;
  margin: 0 4px;
  background-color: #a8e6a1; /* 연한 초록 */
  border-radius: 50%;
  animation: dotFlashing 1s infinite alternate;

  &:nth-child(2) {
    animation-delay: 0.2s;
  }

  &:nth-child(3) {
    animation-delay: 0.4s;
  }

  @keyframes dotFlashing {
    0% {
      background-color: #a8e6a1; /* 연한 초록 */
    }
    50% {
      background-color: #76c893; /* 초록 */
    }
    100% {
      background-color: #3b9d58; /* 진한 초록 */
    }
  }
`;

// 좋아요, 스크랩, 공유 영역
export const HeartScrapDivShare = styled.div`
  width: 100%;
  height: 120px;

  display: flex;
  align-items: center;
  flex-direction: column;

  /* background-color: azure; */
`;

export const HeartScrapWrapperDivShare = styled.div`
  width: 90%;
  height: 44px;

  display: flex;
  justify-content: space-between;

  /* background-color: #2a5dba; */
`;

export const HeartDivScrap = styled.div`
  width: 63px;
  height: 44px;
  border: solid 0px;
  box-sizing: border-box;

  display: flex;
  justify-content: space-between;

  /* background-color: lavender; */
`;

export const OnClickImg = styled.img`
  width: ${(props) => props.width || "27px"};
  height: ${(props) => props.height || "29px"};
  border: solid 0;

  cursor: pointer;
`;

export const HeartDiv = styled.div`
  width: 32px;
  height: 44px;
  border: solid 0px;
  box-sizing: border-box;

  display: flex;
  flex-direction: column;

  /* background-color: darkgrey; */
`;

export const PreNextpostDiv = styled.div`
  width: 265px;
  height: 22px;
  margin-top: 22px;

  display: flex;
  justify-content: space-between;

  /* background-color: burlywood; */
`;

export const OnClickTextspan = styled.span`
  display: inline-block;
  font-size: 17px;
  font-weight: 500;

  cursor: pointer;
`;

// 로그인 전 챗봇
export const NoLoginChatDiv = styled.div`
  width: 45%;
  height: auto;
  position: relative; /* 부모 요소로부터의 상대적 위치 설정 */

  /* 원하는 배경 스타일을 가진 불투명한 레이어 */
  &::before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: #4f4f4f;
    opacity: 0.5;
    z-index: 3; /* 자식 요소 위에 위치하도록 z-index 설정 */
  }

  display: flex;
  justify-content: space-between;
  flex-direction: column;
`;

export const NoticeDiv = styled.div`
  width: 280px;
  height: 138px;
  border: solid 0;
  border-radius: 20px;
  background-color: #ffffff;

  display: flex;
  justify-content: center;
  align-items: center;

  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 4; /* ChatbotDiv의 레이어 위에 위치하도록 z-index 설정 */
`;

export const OneBitImg = styled.img`
  margin-top: 15px;
  width: 100%;
  height: auto;
`;
