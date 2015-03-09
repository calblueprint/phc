class Api::V1::EventRegistrationsController < ApplicationController
  skip_before_action :verify_authenticity_token
  before_action :verify_security_token

  def create
    reg = EventRegistration.new

    # Create account if salesforce id was not passed in
    sf_id = params[:account_sfid]
    if sf_id.nil?
      params[:sf_id] = params.delete :account_sfid # Change key to sf_id
      account = Account.spawn(params)
      reg[:account_sfid] = account.id
    else
      if not Account.exists?(sf_id: sf_id)
        raise "Unknown Salesforce ID. This should not happen!"
      end
      account = Account.find_by(sf_id: params[:account_sfid])
      reg[:account_sfid] = sf_id
    end

    reg.Number__c = params[:Number__c]
    services = ["Acupuncture__c", "Addiction_Recovery__c", "CAAP__c", "Dental__c", \
        "Disability_Services__c", "Employment__c", "Foodbank__c", \
        "Haircuts__c", "Legal__c", "Massage__c", "Medical__c", "Showers__c", \
        "Veteran_Services__c", "Wheelchair_Repair__c" ]
    services.each do |service|
      # Mark service as applied or none if not selected
      status = (if params[service] == "true" then Service.APPLIED else Service.NONE end)
      (reg.services ||= []) << Service.new(name: service, status:status)
    end

    if reg.save
      render :json => { status: "Success" }
    else
      render :json => { status: "Failure" }
    end
  end

  def search
    qr_code = params[:Number__c]
    respond_with EventRegistration.exists?(Number__c: qr_code)
  end

  def update_service
    registration = EventRegistration.find_by(Number__c: params[:Number__c])
    if registration.nil?
      render :json => { status: "Failure", message: "Did not find event registration corresponding to QR code." }
    end

    service = registration.services.find_by(name: params[:service_name])
    if service.nil?
      render :json => { status: "Failure", message: "Did not find specified service for current event." }
      return
    end

    case service.status
    when Service.NONE
      service.update_attribute(:status, Service.DROPIN)
      render :json => { message: "The client is a drop-in." }
      return
    when Service.APPLIED
      service.update_attribute(:status, Service.RECIEVED)
      render :json => { message: "" }
      return
    when Service.RECIEVED
      render :json => { message: "The client has recieved this service before." }
      return
    when Service.DROPIN
      render :json => { message: "The client has recieved this service before." }
      return
    else
      raise "Service status is not known. This should not happen!"
    end
  end

  def update_feedback
  end

end
