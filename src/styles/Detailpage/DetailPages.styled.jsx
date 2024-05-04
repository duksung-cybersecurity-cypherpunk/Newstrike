import styled from 'styled-components';
import { Link } from 'react-router-dom';

const flexCenter = () => `
  display: flex;
  justify-content: center;
`;

export const Div = styled.div`
  width: 100vw;
  height: 100vh;

  ${flexCenter()}
`;

export const BodyDiv = styled.div`
  width: 90vw;
  height: 100vh;
  /* background-color: beige; */
`;

export const NewsDivChat = styled.div`
  width: 100%;
  /* height: auto; */
  margin-top: 10px;
  margin-bottom: 13px;

  display: flex;
  justify-content: space-evenly; //NewsDiv와 ChatbotDiv

  /* background-color: lightgrey; */
`;

// 뉴스레터 영역
export const NewsDiv = styled.div`
  width: 45%;
  /* height: auto; */
  border-radius: 12px;

  /* display: flex; */
  /* justify-content: center;
  align-items: center;
  flex-direction: column; */

  background-color: #cddfab; //고정
`;

export const NewsWrapperDiv = styled.div`
  width: 100%;
  height: ${(props) => props.height || '48px'};
  margin-top: ${(props) => props.margintop || '80px'};
  margin-bottom: ${(props) => props.marginbottom || '0'};
  ${flexCenter()}/* background-color: lightyellow; */
`;

export const NewsTitleWrapperDivDate = styled.div`
  width: 80%;
  height: 48px;

  /* background-color: lightseagreen; */
`;

export const Textspan = styled.span`
  display: ${(props) => props.display || 'block'};
  font-size: ${(props) => props.fontsize || '17px'};
  font-weight: ${(props) => props.fontweight || '500'};
  text-align: ${(props) => props.textalign || 'left'};

  margin-bottom: ${(props) => props.marginbottom || '8px'};

  /* background-color: green; */
`;

export const NewsImg = styled.img`
  width: 90%;
  height: 240px;
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
  height: auto;
  border-radius: 12px;

  display: flex;
  justify-content: space-between;
  flex-direction: column;

  /* background-color: lightcoral; */
`;

export const AllMessageDiv = styled.div`
  width: 100%;
  height: auto;

  display: flex;
  flex-direction: column;

  //스크롤
  max-height: 1000px;
  overflow-y: auto;

  /* background-color: olive; */
`;

export const MessageDiv = styled.div`
  width: 100%;
  height: ${(props) => props.height || '46px'};
  margin-top: ${(props) => props.margintop || '25px'};

  display: flex;
  justify-content: space-between;
  align-items: center;

  /* background-color: greenyellow; */
`;

export const ChatbotImg = styled.img`
  width: ${(props) => props.width || '46px'};
  height: ${(props) => props.height || '46px'};
  border: solid 0;

  cursor: ${(props) => props.cursor || ''};
`;

export const SendspeechbubbleDiv = styled.div`
  width: 50%;
  height: ${(props) => props.height || 'auto'};
  border: solid 0;
  border-radius: 13px;
  padding-left: 10px;

  display: flex;
  align-items: center;

  background-color: #588539;
`;

export const BotspeechbubbleDiv = styled.div`
  width: 380px;
  /* height: ${(props) => props.height || 'auto'}; */
  height: auto;
  border: solid 0;
  border-radius: 16px;
  padding: 10px 10px;

  background-color: #cddfab;
`;

export const AllChattingDiv = styled.div`
  width: 100%;
  height: 45px;
  margin-top: 30px;

  /* background-color: antiquewhite; */
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
  width: ${(props) => props.width || '27px'};
  height: ${(props) => props.height || '29px'};
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
