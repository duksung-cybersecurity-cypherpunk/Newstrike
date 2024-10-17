import React, { useState, useEffect } from "react";
import { OneBitImg } from "../../styles/Detailpage/DetailPages.styled.jsx";

import OneBit1 from "../../images/DetailPage/OneBiteBaseball/1.svg";
import OneBit2 from "../../images/DetailPage/OneBiteBaseball/2.svg";
import OneBit3 from "../../images/DetailPage/OneBiteBaseball/3.svg";
import OneBit4 from "../../images/DetailPage/OneBiteBaseball/4.svg";
import OneBit5 from "../../images/DetailPage/OneBiteBaseball/5.svg";
import OneBit6 from "../../images/DetailPage/OneBiteBaseball/6.svg";
import OneBit7 from "../../images/DetailPage/OneBiteBaseball/7.svg";
import OneBit8 from "../../images/DetailPage/OneBiteBaseball/8.svg";
import OneBit9 from "../../images/DetailPage/OneBiteBaseball/9.svg";
import OneBit10 from "../../images/DetailPage/OneBiteBaseball/10.svg";
import OneBit11 from "../../images/DetailPage/OneBiteBaseball/11.svg";
import OneBit12 from "../../images/DetailPage/OneBiteBaseball/12.svg";
import OneBit13 from "../../images/DetailPage/OneBiteBaseball/13.svg";
import OneBit14 from "../../images/DetailPage/OneBiteBaseball/14.svg";
import OneBit15 from "../../images/DetailPage/OneBiteBaseball/15.svg";
import OneBit16 from "../../images/DetailPage/OneBiteBaseball/16.svg";
import OneBit17 from "../../images/DetailPage/OneBiteBaseball/17.svg";
import OneBit18 from "../../images/DetailPage/OneBiteBaseball/18.svg";
import OneBit19 from "../../images/DetailPage/OneBiteBaseball/19.svg";
import OneBit20 from "../../images/DetailPage/OneBiteBaseball/20.svg";
import OneBit21 from "../../images/DetailPage/OneBiteBaseball/21.svg";

export default function OneBitBaseball() {
  const oneBitImages = [
    OneBit1,
    OneBit2,
    OneBit3,
    OneBit4,
    OneBit5,
    OneBit6,
    OneBit7,
    OneBit8,
    OneBit9,
    OneBit10,
    OneBit11,
    OneBit12,
    OneBit13,
    OneBit14,
    OneBit15,
    OneBit16,
    OneBit17,
    OneBit18,
    OneBit19,
    OneBit20,
    OneBit21,
  ];
  const [currentImage, setCurrentImage] = useState(oneBitImages[0]);

  useEffect(() => {
    const interval = setInterval(() => {
      const randomIndex = Math.floor(Math.random() * oneBitImages.length);
      setCurrentImage(oneBitImages[randomIndex]);
    }, 10000); //10초

    return () => clearInterval(interval);
  }, [oneBitImages]);
  return (
    <>
      <OneBitImg src={currentImage} alt="랜덤 한입야구 이미지" />
    </>
  );
}
