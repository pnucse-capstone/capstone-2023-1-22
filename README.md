[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-24ddc0f5d75046c5622901739e7c5dd533143b0c8e959d652212380cedb1ea36.svg)](https://classroom.github.com/a/fnZ3vxy8)
## 1. 프로젝트 소개
본 졸업 과제에서는 C 소프트웨어의 소스코드를 기반으로 소프트웨어의 품질을 측정하는 도구를 개발하는 것을 목표로 한다. 소프트웨어의 품질을 자동으로 측정하기 위해 코딩 표준을 분석하여 표준에 정의된 코딩 규칙과 소프트웨어 품질 속성 간의 관계를 분석한다. 그리고 기존의 정적 분석 도구를 이용해 코딩 표준 위반 정보를 수집하고 수집된 위반 정보를 기반으로 자동으로 품질 속성을 측정한다. 
## 2. 팀 소개
201624567 전민기<br>
jmg2576@pusan.ac.kr<br>
CERT C 분석 및 매핑 테이블 작성
시연 영상 제작, 포스터 제작, 보고서 작성

201724492 성민우<br> 
surplusghost@pusan.ac.kr<br>
MISRA C 분석 및 매핑 테이블 작성
품질 점수 시각화 프로그램 개발
포스터 제작, 보고서 작성

201524593 천동혁<br>
cjs9615@pusan.ac.kr<br>
ISO5055 분석 및 매핑 테이블 작성
포스터 제작, 보고서 작성

## 3. 구성도
![깃헙구성도](https://github.com/pnucse-capstone/capstone-2023-1-22/assets/80205593/d28a15b1-3dbe-459a-8b03-106325fe565e)
<br>
(1)	Polyspace 프로그램을 이용하여 C 소스코드의 정적 분석을 수행한다. <br><br>
(2)	MISRA-C, CERT C, ISO 5055를 활용하여 품질 요구사항과 코딩규칙을 매핑한다. <br><br>
(3)	위의 두 결과를 이용하여 품질 지수 측정 계산법을 개발한다. <br><br>
(4)	점수를 시각화하기 위한 프로그램을 만든다.

## 4. 소개 및 시연 영상
## 5. 사용법

* 실행 환경<br>
본 프로젝트는 Java Development Kit 17 이상의 InteliJ 환경에서 개발되었으며 이외의 환경에서 소스코드를 통해 실행할 경우 SoftwareQuality 경로 내부의 jfreechart-1.5.4.jar 라이브러리를 사용하기위해 링크하여야함.


* 사용법
1. 품질을 측정하고자 하는 C 소프트웨어의 소스코드를 Polyspace를 통하여 분석한다. 이 때 Code Prover에서는 Code Metric을 측정하고 Bug Finder에서는 Misra C와 Cert C, CWE를 체크하도록 설정한다.<br>
2. 결과를 Export하여 txt파일을 얻는다.<br>
3. 프로그램을 실행한 이후 좌측 상단의 메뉴를 클릭하고 Create를 선택하여 이름을 입력하고 앞서 얻은 두 txt파일을 선택하면 측정 결과를 얻을수 있다.
